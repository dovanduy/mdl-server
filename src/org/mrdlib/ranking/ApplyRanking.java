package org.mrdlib.ranking;

import java.util.Random;

import org.mrdlib.Constants;
import org.mrdlib.UnknownException;
import org.mrdlib.database.DBConnection;
import org.mrdlib.display.DisplayDocument;
import org.mrdlib.display.DocumentSet;
import org.mrdlib.display.StatusReportSet;
import org.mrdlib.solrHandler.solrConnection;

/**
 * @author Millah
 * 
 *         This class executes the different ranking values based on Alt-, and
 *         Bibliometrics.
 *
 */
public class ApplyRanking {

	private DBConnection con = null;
	private solrConnection scon = null;
	private Constants constants = null;
	private StatusReportSet statusReportSet = null;
	
	private int rndAlgorithmRows; 
	private int rndWeight;
	private int rndRank;
	private int algorithmRows;
	private int rndDisplayNumber;

	/**
	 * 
	 * initialize random numbers and initialize solr connection
	 * 
	 * @param database connection
	 */
	public ApplyRanking(DBConnection con) {
		constants = new Constants();
		this.con = con;
	
		Random random = new Random();
		//random number for the number of considered results from the algorithm
		rndAlgorithmRows = random.nextInt(7)+1;
		//random number for the proportion of text relevance score and alt/bibliometric
		rndWeight = random.nextInt(8)+1;
		//random number for the chosen metric
		rndRank = random.nextInt(4)+1;
		
		//choose a number of considered results from the algorithm
		switch (rndAlgorithmRows) {
		case 1:
			algorithmRows = 10; break;
		case 2:
			algorithmRows = 20; break;
		case 3:
			algorithmRows = 30; break;
		case 4:
			algorithmRows = 40; break;
		case 5:
			algorithmRows = 50; break;
		case 6:
			algorithmRows = 75; break;
		case 7:
			algorithmRows = 100; break;
		default:
			algorithmRows = 200;
		}
		try {
			scon = new solrConnection(con);
		} catch (Exception e) {
			statusReportSet.addStatusReport(new UnknownException(e, constants.getDebugModeOn()).getStatusReport());
		}
	}

	/**
	 * 
	 * reranks the documentset from the algorithm with random parameters
	 * 
	 * @param documentSet by algorithm
	 * @return reranked documentSet
	 * @throws Exception
	 */
	public DocumentSet selectRandomRanking(DocumentSet documentSet) throws Exception {
		boolean onlySolr = false;
		Random random = new Random();
		
		//random number for the number of displayed recommendations
		rndDisplayNumber = random.nextInt(15)+1;

		
		//documentset = scon.getRelatedDocumentSetByDocument(requestDocument, solrRows);

		//if the algorithm does not provide enough result, fall back on the biggest fitting enum from database
		if (documentSet.getSize() < algorithmRows)
			algorithmRows = getNextTinierAlgorithmRows(documentSet.getSize());  //CHANGED THIS HERE BECAUSE FOR STEREOTYPE RECOMMENDATIONS, WE 
		//CAN ONLY GET AROUND 60 recommendations maximum
		
		documentSet.setNumberOfSolrRows(algorithmRows);
		
		//if there are more results than wanted, cut the list
		if (documentSet.getSize() > algorithmRows-1)
			documentSet.setDocumentList(documentSet.getDocumentList().subList(0, algorithmRows - 1));

		//choose a ranking metric
		switch (rndRank) {
		case 1:
			documentSet = getAltmetric(documentSet, "simple_count", "readers", "mendeley"); break;
		case 2:
			documentSet = getAltmetric(documentSet, "normalizedByAge", "readers", "mendeley"); break;
		case 3:
			documentSet = getAltmetric(documentSet, "normalizedByNumberOfAuthors", "readers", "mendeley"); break;
		case 4:
			documentSet = getSolr(documentSet); onlySolr=true; rndWeight = random.nextInt(2)+1; break;
		default:
			documentSet = getSolr(documentSet); onlySolr=true; rndWeight = random.nextInt(2)+1; break;
		}
		
		//find out how much of the documents have a alt/bibliometric
		documentSet.calculatePercentageRankingValue();
		
		//choose a proportion of text relevance score and alt/bibliometric
		switch (rndWeight) {
		case 1:
			documentSet.sortAscForRankingValue(onlySolr); break;
		case 2:
			documentSet.sortDescForRankingValue(onlySolr); break;
		case 3:
			documentSet.sortDescForLogRankingValueTimesTextRelevance(); break;
		case 4:
			documentSet.sortDescForRootRankingValueTimesTextRelevance(); break;
		case 5:
			documentSet.sortDescForRankingValueTimesTextRelevance(); break;
		case 6:
			documentSet.sortAscForRankingValueTimesTextRelevance(); break;
		case 7:
			documentSet.sortAscForLogRankingValueTimesTextRelevance(); break;
		case 8:
			documentSet.sortAscForRootRankingValueTimesTextRelevance(); break;
		default:
			documentSet.sortDescForRankingValue(onlySolr); break;
		}
		
		//cut the list to the number we want to display
		if(documentSet.getSize() > rndDisplayNumber)
			documentSet.setDocumentList(documentSet.getDocumentList().subList(0, rndDisplayNumber));
		
		return documentSet.refreshRankBoth();
	}
	

	/**
	 * 
	 * if the algorithm provides not enough results, find biggest enum from database which fits
	 * 
	 * @param expected algorithmRow
	 * @return new tinier algorithmRow 
	 */
	private int getNextTinierAlgorithmRows(int algorithmRows) {

		if (algorithmRows < 20)
			algorithmRows = 10;
		else if (algorithmRows < 30)
			algorithmRows = 20;
		else if (algorithmRows < 40)
			algorithmRows = 30;
		else if (algorithmRows < 50)
			algorithmRows = 40;
		else if (algorithmRows < 75)
			algorithmRows = 50;
		else if (algorithmRows < 100)
			algorithmRows = 75;
		else if (algorithmRows < 200)
			algorithmRows = 200;

		return algorithmRows;
	}

	/**
	 * 
	 * get the specified altmetrics for all documents in a document set
	 * 
	 * @param documentSet
	 * @param metric, eg simple_count
	 * @param type, eg readership
	 * @param source, eg mendeley
	 * @return DocumentSet with attached rankingValues, -1 if no rankingValue
	 */
	public DocumentSet getAltmetric(DocumentSet documentset, String metric, String type, String source) {
		DisplayDocument current = null;
		DisplayDocument temp = new DisplayDocument(constants);

		for (int i = 0; i < documentset.getSize(); i++) {
			current = documentset.getDocumentList().get(i);
			temp = con.getRankingValue(current.getDocumentId(), metric, type, source);
			current.setRankingValue(temp.getRankingValue());
			current.setBibId(temp.getBibId());
		}
		return documentset;
	}
	
	/**
	 * 
	 * wrapper method to set ranking value to textrelevance score
	 * 
	 * @param documentSet
	 * @return DocumentSet with attached rankingValues
	 */
	public DocumentSet getSolr(DocumentSet documentset) {
		DisplayDocument current = null;

		for (int i = 0; i < documentset.getSize(); i++) {
			current = documentset.getDocumentList().get(i);
			current.setRankingValue(current.getTextRelevancyScore());
		}
		return documentset;
	}
	
	public int getRndSolrRows() {
		return rndAlgorithmRows;
	}

	public int getRndWeight() {
		return rndWeight;
	}

	public int getRndRank() {
		return rndRank;
	}
	
	public int getSolrRows() {
		return algorithmRows;
	}

}
