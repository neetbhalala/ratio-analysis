package com.example.FinanceAnalyzer;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static java.util.stream.Collectors.joining;

@SpringBootApplication
public class FinanceAnalyzerApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(FinanceAnalyzerApplication.class, args);

//		Path documentPath = toPath("/Emcure_BS_page.pdf");
//		log.info("Loading single document: {}", documentPath);
//		Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());

//		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
//
//		EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//
//		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
//				.documentSplitter(DocumentSplitters.recursive(300, 0))
//				.embeddingModel(embeddingModel)
//				.embeddingStore(embeddingStore)
//				.build();

/*		Document document = loadPDF("C:/Users/baps/Desktop/BEL_BS_page.pdf");
//
//		ingestor.ingest(document);
//
		DocumentSplitter splitter = DocumentSplitters.recursive(
				500,
				0,
				new OpenAiTokenizer("gpt-3.5-turbo")
            );
		List<TextSegment> segments = splitter.split(document);
//
//		// Embed segments (convert them into vectors that represent the meaning) using embedding model
		EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
		List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

		EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
		embeddingStore.addAll(embeddings, segments);

		String question = "Extract the complete Balance Sheet from the document, including all items listed under Assets like current assets and non-current assets, Liabilities like current liabilities and non-current liabilities, and Equity, along with their corresponding values. Ensure that no data points are missed, and present the information in a structured format. Empty values should be given null.";

		Embedding questionEmbedding = embeddingModel.embed(question).content();

//		// Find relevant embeddings in embedding store by semantic similarity
//		// You can play with parameters below to find a sweet spot for your specific use case
		int maxResults = 65;
		double minScore = 0.4;
		List<EmbeddingMatch<TextSegment>> relevantEmbeddings
				= embeddingStore.findRelevant(questionEmbedding, maxResults, minScore);
//		// Create a prompt for the model that includes question and relevant embeddings
//		PromptTemplate promptTemplate = PromptTemplate.from(
//				"Answer the following question to the best of your ability:\n"
//						+ "\n"
//						+ "Question:\n"
//						+ "{{question}}\n"
//						+ "\n"
//						+ "Give answer from the following information in structured format without missing any data points:\n"
//						+ "{{information}}");
		PromptTemplate promptTemplate = PromptTemplate.from(
				"Answer the following question to the best of your ability:\n\n"
						+ "Question:\n"
						+ "{{question}}\n\n"
						+ "Based on the following information, provide a structured response in JSON format. "
						+ "Ensure that the data is organized as follows:\n"
						+ "{\n"
						+ "  \"assets\": {\n"
						+ "    \"nonCurrentAssets\": {\"category\": value},\n"
						+ "    \"currentAssets\": {\"category\": value},\n"
						+ "    \"totalNonCurrentAssets\": value,\n"
						+ "    \"totalCurrentAssets\": value,\n"
						+ "    \"totalAssets\": value\n"
						+ "  },\n"
						+ "  \"equity\": {\n"
						+ "    \"categories\": {\"category\": value},\n"
						+ "    \"totalEquity\": value\n"
						+ "  },\n"
						+ "  \"liabilities\": {\n"
						+ "    \"nonCurrentLiabilities\": {\"category\": value},\n"
						+ "    \"currentLiabilities\": {\"category\": value},\n"
						+ "    \"totalNonCurrentLiabilities\": value,\n"
						+ "    \"totalCurrentLiabilities\": value,\n"
						+ "    \"totalLiabilities\": value\n"
						+ "  },\n"
						+ "  \"totalEquityAndLiabilities\": value\n"
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
//		System.out.println(answer);

////		ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
////				.chatLanguageModel(OpenAiChatModel.withApiKey(Constant.OPENAI_API_KEY))
////				.retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
//				// .chatMemory() // you can override default chat memory
//				// .promptTemplate() // you can override default prompt template
////				.build();
//
////		String answer = chain.execute("Extract the complete Balance Sheet from the document, including all items listed under Assets, Liabilities, and Equity, along with their corresponding values. Ensure that no data points are missed, and present the information in a structured format.");
//
		// Parse the answer string into a JSONObject
		JSONObject jsonAnswer = new JSONObject(answer);

// Print the formatted JSON string
		System.out.println(jsonAnswer.toString(2));

		storeData(jsonAnswer);

*/

//		FinancialReport financialReport = new FinancialReport();
//
//		financialReport.setCompanyName("ABC Corp");
//		financialReport.setFiscalYear("2023");
//
//		ProfitAndLoss profitAndLoss = new ProfitAndLoss();
//		profitAndLoss.setRevenueFromOperations("500000");
//		profitAndLoss.setOtherIncome("10000");
//		profitAndLoss.setTotalRevenue("510000");
//		profitAndLoss.setCostOfMaterialsConsumed("300000");
//		profitAndLoss.setPurchasesOfStockInTrade("20000");
//		profitAndLoss.setChangesInInventories("-5000");
//		profitAndLoss.setEmployeeBenefitsExpense("50000");
//		profitAndLoss.setFinanceCosts("2000");
//		profitAndLoss.setDepreciationAndAmortizationExpense("15000");
//		profitAndLoss.setOtherExpenses("40000");
//		profitAndLoss.setTotalExpenses("422000");
//		profitAndLoss.setProfitBeforeTax("88000");
//		profitAndLoss.setTaxExpense("22000");
//		profitAndLoss.setProfitAfterTax("66000");
//
//		BalanceSheet balanceSheet = new BalanceSheet();
//		balanceSheet.setPropertyPlantAndEquipment("150000");
//		balanceSheet.setCapitalWorkInProgress("20000");
//		balanceSheet.setIntangibleAssets("5000");
//		balanceSheet.setInvestments("30000");
//		balanceSheet.setLoansNonCurrent("10000");
//		balanceSheet.setDeferredTaxAssetsNet("2000");
//		balanceSheet.setOtherNonCurrentAssets("5000");
//		balanceSheet.setInventories("25000");
//		balanceSheet.setTradeReceivables("35000");
//		balanceSheet.setCashAndCashEquivalents("15000");
//		balanceSheet.setBankBalancesOtherThanCashAndCashEquivalents("5000");
//		balanceSheet.setLoansCurrent("2000");
//		balanceSheet.setOtherCurrentAssets("3000");
//		balanceSheet.setEquityShareCapital("50000");
//		balanceSheet.setOtherEquity("70000");
//		balanceSheet.setBorrowingsNonCurrent("20000");
//		balanceSheet.setDeferredTaxLiabilitiesNet("3000");
//		balanceSheet.setOtherNonCurrentLiabilities("5000");
//		balanceSheet.setBorrowingsCurrent("10000");
//		balanceSheet.setTradePayables("15000");
//		balanceSheet.setOtherFinancialLiabilitiesCurrent("4000");
//		balanceSheet.setOtherCurrentLiabilities("5000");
//		balanceSheet.setProvisions("2000");
//
//		financialReport.setProfitAndLoss(profitAndLoss);
//		financialReport.setBalanceSheet(balanceSheet);
//
//		// Convert to prompt
//		Prompt prompt = StructuredPromptProcessor.toPrompt(financialReport);
//
//		// Generate JSON from AI model
//		ChatLanguageModel model = OpenAiChatModel.builder()
//				.apiKey(Constant.OPENAI_API_KEY)
//				.timeout(Duration.ofSeconds(60))
//				.build();
//		AiMessage aiMessage = model.generate(prompt.toUserMessage()).content();
//		System.out.println(aiMessage.text());

	}


//	public static Document loadPDF(String pdfFilePath) throws IOException {
//		PDDocument pdfDocument = PDDocument.load(new File(pdfFilePath));
//		PDFTextStripper pdfStripper = new PDFTextStripper();
//		String text = pdfStripper.getText(pdfDocument);
//		pdfDocument.close();
//
//		return new Document(text); // Assuming your Document class can take raw text
//	}

//	private static Path toPath(String fileName) {
//		try {
//			URL fileUrl = FinanceAnalyzerApplication.class.getResource(fileName);
//			return Paths.get(fileUrl.toURI());
//		} catch (URISyntaxException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private static void storeData(JSONObject jsonObject) throws IOException {
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
//		okhttp3.RequestBody body = RequestBody.create(mediaType, jsonObject);
		RequestBody body = RequestBody.create(mediaType, String.valueOf(jsonObject));
		Request request = new Request.Builder()
				.url("https://app.nocodb.com/api/v2/tables/mh8lcp30rckg03t/records")
				.method("POST", body)
				.addHeader("xc-token", "7UKKVAcXGDGTjgXE_PCLPRo32juVtpw_itA6BJ6E")
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer 7UKKVAcXGDGTjgXE_PCLPRo32juVtpw_itA6BJ6E")
				.build();
		Response response = client.newCall(request).execute();
	}




}

