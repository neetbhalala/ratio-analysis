package com.example.FinanceAnalyzer.service;

import com.example.FinanceAnalyzer.utils.Constant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.util.stream.Collectors.joining;

@Service
public class ExtractionService {

    @Autowired
    private DataParserService dataParserService;

    public void processPDF(MultipartFile file, String companyName) throws IOException {

        Document document = loadPDF(file);

        DocumentSplitter splitter = DocumentSplitters.recursive(
                500,
                0,
                new OpenAiTokenizer("gpt-3.5-turbo")
        );
        List<TextSegment> segments = splitter.split(document);

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        String question = "Extract the complete Balance Sheet from the document, including all items listed under Assets like current assets and non-current assets, Liabilities like current liabilities and non-current liabilities, and Equity, along with their corresponding values. Ensure that no data points are missed, and present the information in a structured format. Empty values should be given null.";

        Embedding questionEmbedding = embeddingModel.embed(question).content();

        int maxResults = 50;
        double minScore = 0.4;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings
                = embeddingStore.findRelevant(questionEmbedding, maxResults, minScore);

        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question to the best of your ability:\n\n"
                        + "Question:\n"
                        + "{{question}}\n\n"
                        + "Based on the following information, provide a structured response in JSON format. "
                        + "Ensure that the data is organized as follows:\n"
                        + "{\n"
                        + "    \"nonCurrentAssets\": {\"category\": value},\n"
                        + "    \"currentAssets\": {\"category\": value},\n"
                        + "    \"totalNonCurrentAssets\": value,\n"
                        + "    \"totalCurrentAssets\": value,\n"
                        + "    \"totalAssets\": value,\n"
                        + "    \"equity\": {\"category\": value},\n"
                        + "    \"totalEquity\": value,\n"
                        + "    \"nonCurrentLiabilities\": {\"category\": value},\n"
                        + "    \"currentLiabilities\": {\"category\": value},\n"
                        + "    \"totalNonCurrentLiabilities\": value,\n"
                        + "    \"totalCurrentLiabilities\": value,\n"
                        + "    \"totalLiabilities\": value,\n"
                        + "    \"totalEquityAndLiabilities\": value\n"
                        + "}\n\n"
                        + "Please structure the response exactly in this format, ensuring all data points are included:\n"
                        + "{{information}}"
        );
        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);
        Prompt prompt = promptTemplate.apply(variables);

        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(Constant.OPENAI_API_KEY)
                .build();
        AiMessage aiMessage = chatModel.generate(prompt.toUserMessage()).content();
        String answer = aiMessage.text();

        JSONObject jsonAnswer = new JSONObject(answer);
        System.out.println(jsonAnswer.toString(2));
        // Step 1: Duplicate the base with the company name
        String baseId = dataParserService.duplicateBase(companyName);

        // Step 2: Store data in the duplicated base's Balance Sheet table
        dataParserService.storeData(jsonAnswer, baseId);
    }


    private Document loadPDF(MultipartFile file) throws IOException {
        PDDocument pdfDocument = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(pdfDocument);
        pdfDocument.close();
        return new Document(text);
    }

}
