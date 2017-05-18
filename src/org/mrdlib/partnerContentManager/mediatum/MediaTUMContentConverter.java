package org.mrdlib.partnerContentManager.mediatum;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.mrdlib.partnerContentManager.IContentConverter;
import org.mrdlib.partnerContentManager.gesis.Tuple;

/**
 * Implementation of ContentConverter for partner mediaTUM.
 * mediaTUM offers a standardized OAI interface exhibiting data in the OAI Dublin Core format (http://www.openarchives.org/OAI/openarchivesprotocol.html).
 * 
 * @author wuestehube
 *
 */
public class MediaTUMContentConverter implements IContentConverter<MediaTUMXMLDocument> {

	/**
	 * Creates the mapping of type codes used in mediaTUM and Mr. DLib. Types are the publication types.
	 * 
	 * @return a type map that can be used in processing XML documents
	 */
	private Map<String, String> createTypeMap() {
		Map<String, String> typeMap = new HashMap<String, String>();
		
		typeMap.put("report", "report");
		typeMap.put("thesis_unspecified", "thesis_unspecified");
		typeMap.put("thesis_doctoral", "thesis_doctoral");
		typeMap.put("thesis_master", "thesis_master");
		typeMap.put("article", "article");
		typeMap.put("thesis_bachelor", "thesis_bachelor");
		typeMap.put("unknown", "unknown");
		
		return typeMap;
	}
	
	/**
	 * Creates the mapping of language codes used in mediaTUM and Mr. DLib.
	 * 
	 * @return a language map that can be used in processing XML documents
	 */
	private Map<String, String> createLanguageMap() {
		Map<String, String> languageMap = new HashMap<String, String>();
		
		languageMap.put("(eng)", "(en)");
		languageMap.put("eng", "(en)");
		languageMap.put("(deu)", "(de)");
		languageMap.put("deu", "(de)");
		languageMap.put("(ger)", "(de)");
		languageMap.put("ger", "(de)");
		languageMap.put("(spa)", "(es)");
		languageMap.put("spa", "(es)");
		languageMap.put("(fra)", "(fr)");
		languageMap.put("fra", "(fr)");
		languageMap.put("(zho)", "(zh)");
		languageMap.put("zho", "(zh)");
		languageMap.put("(jpn)", "(ja)");
		languageMap.put("jpn", "(ja)");
		languageMap.put("(rus)", "(ru)");
		languageMap.put("rus", "(ru)");
		
		return languageMap;
	}
	
	/**
	 * Creates the mapping of type resolve used in mediaTUM and Mr. DLib.
	 * 
	 * @return a type resolve map that can be used in processing XML documents
	 */
	private Map<Tuple, String> createTypeResolveMap() {
		HashMap<Tuple, String> typeResolveMap = new HashMap<Tuple, String>();
		
		// not needed in case of mediaTUM
		
		return typeResolveMap;
	}
	
	@Override
	public MediaTUMXMLDocument convertPartnerContentToStorablePartnerContent(String pathOfFileToConvert) {
		// extract information
		OAIDCRecord oaidcRecord = readOAIDCRecordFromFile(pathOfFileToConvert);
		
		ArrayList<String> abstracts = getAbstractsFromOAIDCRecord(oaidcRecord);
		String language = getLanguageFromOAIDCRecord(oaidcRecord);
		String idOriginal = getIdOriginalFromOAIDCRecord(oaidcRecord);
		String title = getTitleFromOAICDRecord(oaidcRecord);
		String fulltitle = getTitleFromOAICDRecord(oaidcRecord);
		String year = getYearFromOAIDCRecord(oaidcRecord);
		String facetYear = getYearFromOAIDCRecord(oaidcRecord);
		ArrayList<String> authors = getAuthorsFromOAIDCRecord(oaidcRecord);
		ArrayList<String> keyWords = getKeyWordsFromOAIDCRecord(oaidcRecord);
		String type = getTypeFromOAIDCRecord(oaidcRecord);
		String publishedIn = getPublishedInFromOAIDCRecord(oaidcRecord);
		String collection = getCollectionFromOAIDCRecord(oaidcRecord);
		
		// check for errors
		if ((abstracts == null) || (language == null) || (idOriginal == null) || (title == null) ||
				(fulltitle == null) || (year == null) || (facetYear == null) || (authors == null) ||
				(keyWords == null) || (type == null) || (publishedIn == null) || (collection == null)) {
			return null;
		}
		
		// set up XML document
		Map<String, String> typeMap = createTypeMap();
		Map<String, String> languageMap = createLanguageMap();
		Map<Tuple, String> typeResolveMap = createTypeResolveMap();
		
		MediaTUMXMLDocument xmlDocument = new MediaTUMXMLDocument(typeMap, languageMap, typeResolveMap);
		
		for (String abstract_ : abstracts) {
			xmlDocument.addAbstract(abstract_.split(Pattern.quote("|"))[1], abstract_.split(Pattern.quote("|"))[0]);
		}
		xmlDocument.setId(idOriginal);
		xmlDocument.setTitle(title);
		xmlDocument.setFulltitle(fulltitle);
		xmlDocument.setLanguage(language);
		xmlDocument.setYear(year);
		xmlDocument.setFacetYear(facetYear);
		for (String author : authors) {
			author = author.split(Pattern.quote("("))[0].trim();
			xmlDocument.addAuthor(author);
		}
		for (String keyWord : keyWords) {
			xmlDocument.addKeyWord(keyWord);
		}
		xmlDocument.addType(type);
		xmlDocument.setPublishedIn(publishedIn, "publisher");
		xmlDocument.setCollection(collection);
		
		xmlDocument.normalize();
		
		return xmlDocument;
	}
	
	/**
	 * Reads in a given file and converts it to an OAIDC record.
	 * 
	 * @param pathOfFile file to read in
	 * @return OAIDC record
	 */
	private OAIDCRecord readOAIDCRecordFromFile(String pathOfFile) {
		OAIDCRecord oaidcRecord = new OAIDCRecord();
		
		File file = new File(pathOfFile);
		
		try {
            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // attribute found
                if (line.contains("<dc:")) {
                	String attributeName = line.split("<dc:")[1].split(">")[0].split(" ")[0];
                	
                	// take multi lines into account
                	int i = 0;
                	while (!line.contains("</dc")) {
                		if (scanner.hasNextLine()) {
                			line += " " + scanner.nextLine();
                		}
                		
                		// for safety
                		i++;
                		if (i > 100) {
                			break;
                		}
                	}
                	
                	String attributeValue = line.split(">")[1].split("</dc")[0];
                	
                	if (i > 0) {
                		attributeValue = line.substring(line.indexOf(">")).split("</dc")[0];
                	}
                	
                	// remove tags
                	attributeValue = attributeValue.replaceAll("<br>", " ");
                	attributeValue = attributeValue.replaceAll(Pattern.quote("<i>"), "");
                	attributeValue = attributeValue.replaceAll(Pattern.quote("</i>"), "");
                	attributeValue = attributeValue.replaceAll(Pattern.quote("<sup>"), "");
                	attributeValue = attributeValue.replaceAll(Pattern.quote("</sup>"), "");
                	if (attributeValue.contains("<![CDATA[]]")) {
                		attributeValue = "";
                	}
                	if (attributeValue.contains("<![CDATA[")) {
                		attributeValue = attributeValue.split(Pattern.quote("![CDATA["))[1].split(Pattern.quote("]]"))[0];
                	}
                	
                	if (!attributeValue.equals("")) {                		
                		switch (attributeName) {
						case "title":
							oaidcRecord.addTitle(attributeValue);
							break;
						case "creator":
							oaidcRecord.addCreator(attributeValue);
							break;
						case "subject":
							for (String subject : attributeValue.split(", ")) {
								oaidcRecord.addSubject(subject);
							}
							break;
						case "description":
							// default language
							String descriptionLanguage = "de";
							
							if (line.contains("xml:lang")) {
								 descriptionLanguage = line.split(Pattern.quote("<dc:description xml:lang="))[1].split(">")[0].replaceAll("\"", "");
							}
							
							oaidcRecord.addDescription(descriptionLanguage + "|" + attributeValue);
							break;
						case "publisher":
							oaidcRecord.addPublisher(attributeValue);
							break;
						case "contributor":
							oaidcRecord.addContributor(attributeValue);
							break;
						case "date":
							oaidcRecord.addDate(attributeValue);
							break;
						case "type":
							oaidcRecord.addType(attributeValue);
							break;
						case "format":
							oaidcRecord.addFormat(attributeValue);
							break;
						case "identifier":
							if (oaidcRecord.getIdentifiers().size() == 0) {
								oaidcRecord.addIdentifier(attributeValue.split("id=")[1]);
							}
							break;
						case "source":
							oaidcRecord.addSource(attributeValue);
							break;
						case "language":
							oaidcRecord.addLanguage(attributeValue);
							break;
						case "relation":
							oaidcRecord.addRelation(attributeValue);
							break;
						case "coverage":
							oaidcRecord.addCoverage(attributeValue);
							break;
						case "right":
							oaidcRecord.addRight(attributeValue);
							break;

						default:
							break;
						}
                	}
                }
            }
            
            scanner.close();
            
            return oaidcRecord;
        } catch (FileNotFoundException e) {
            return null;
        }
	}
	
	private ArrayList<String> getAbstractsFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		ArrayList<String> abstracts = new ArrayList<String>();
		
		if (oaidcRecord.getDescriptions().size() > 0) {
			abstracts = oaidcRecord.getDescriptions();
		} else {
			// a common case
		}
		
		for (int i = 0; i < abstracts.size(); i++) {
			abstracts.set(i, replaceSpecialCharacters(abstracts.get(i)));
		}
		
		return abstracts;
	}
	
	private String getLanguageFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		String language = "";
		
		if (oaidcRecord.getLanguages().size() > 0) {
			language = oaidcRecord.getLanguages().get(0);
		} else {
			System.out.println("Error: no language found.");
			return null;
		}
		
		return language;
	}
	
	private String getIdOriginalFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		String idOrignal = "";
		
		if (oaidcRecord.getIdentifiers().size() > 0) {
			idOrignal = "mt" + oaidcRecord.getIdentifiers().get(0);
		} else {
			System.out.println("Error: no identifier found.");
			return null;
		}
		
		return idOrignal;
	}
	
	private String getTitleFromOAICDRecord(OAIDCRecord oaidcRecord) {
		String title = "";
		
		if (oaidcRecord.getTitles().size() > 0) {
			title = oaidcRecord.getTitles().get(0);
		} else {
			System.out.println("Error: no title found.");
			return null;
		}
		
		title = replaceSpecialCharacters(title);
		
		return title;
	}
	
	private String getYearFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		String year = "";
		
		if (oaidcRecord.getDates().size() > 0) {
			year = Integer.toString(getPublicationYearFromOAIDCDateFormat(oaidcRecord.getDates().get(0)));
		} else {
			System.out.println("Error: no year found.");
			return null;
		}
		
		return year;
	}
	
	/**
	 * Extracts the year from a OAI DC date format (yyyy-mm-dd).
	 * 
	 * @param oaiDate date in OAI DC format (yyyy-mm-dd) to extract year from
	 * @return extracted year
	 */
	private int getPublicationYearFromOAIDCDateFormat(String oaiDate) {
		return Integer.parseInt(oaiDate.substring(0, 4));
	}
	
	private ArrayList<String> getAuthorsFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		ArrayList<String> authors = new ArrayList<String>();
		
		authors = oaidcRecord.getCreators();
		authors.addAll(oaidcRecord.getContributors());
		
		for (int i = 0; i < authors.size(); i++) {
			authors.set(i, replaceSpecialCharacters(authors.get(i)));
		}
		
		return authors;
	}
	
	private ArrayList<String> getKeyWordsFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		ArrayList<String> keyWords = new ArrayList<>();
		
		for (String subject : oaidcRecord.getSubjects()) {
			for (String keyWord : subject.split(";")) {
				keyWords.add(replaceSpecialCharacters(keyWord));
			}
		}
		
		return keyWords;
	}
	
	private String getTypeFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		String type = "";
		
		if (oaidcRecord.getTypes().size() > 0) {
			switch (oaidcRecord.getTypes().get(0)) {
			case "doc-type:report":
				type = "report";
				break;
			case "thesis":
				type = "thesis_unspecified";
				break;
			case "report":
				type = "report";
				break;
			case "dissertation":
				type = "thesis_doctoral";
				break;
			case "doc-type:masterThesis":
				type = "thesis_master";
				break;
			case "doc-type:doctoralThesis":
				type = "thesis_doctoral";
				break;
			case "article":
				type = "article";
				break;
			case "doc-type:bachelorThesis":
				type = "thesis_bachelor";
				break;
			default:
				type = "unknown";
				break;
			}
		} else {
			System.out.println("Error: no type found.");
			return null;
		}
		
		return type;
	}
	
	private String getPublishedInFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		String publishedIn = "";
		
		if (oaidcRecord.getPublishers().size() > 0) {
			publishedIn = oaidcRecord.getPublishers().get(0);
		} else {
			System.out.println("Warning: no publishers found.");
		}
		
		publishedIn = replaceSpecialCharacters(publishedIn);
		
		return publishedIn;
	}
	
	private String replaceSpecialCharacters(String stringToReplaceUmlautsIn) {
		/* thanks to http://stackoverflow.com/questions/4122170/java-change-%C3%A1%C3%A9%C5%91%C5%B1%C3%BA-to-aeouu */		
		stringToReplaceUmlautsIn = Normalizer.normalize(stringToReplaceUmlautsIn, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
		
		return stringToReplaceUmlautsIn;
	}
	
	private String getCollectionFromOAIDCRecord(OAIDCRecord oaidcRecord) {
		// default value
		String collection = "000";
		
		int numSubjects = oaidcRecord.getSubjects().size();
		
		if (numSubjects > 0) {
			String lastSubject = oaidcRecord.getSubjects().get(numSubjects-1);
			
			if (lastSubject.contains("ddc:")) {
				collection = lastSubject.split("ddc:")[1];
			}
		}
		
		return "mediatum-ddc" + collection;
	}
	
}
