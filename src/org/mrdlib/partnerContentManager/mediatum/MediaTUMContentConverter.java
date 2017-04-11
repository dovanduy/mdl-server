package org.mrdlib.partnerContentManager.mediatum;

import java.util.Date;
import java.util.List;

import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentExternalIdExternalName;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentKeyphraseSource;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentTranslatedFieldFieldType;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentTranslatedFieldTranslationTool;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentType;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocument;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentAbstract;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentExternalId;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentKeyphrase;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentKeyphraseCount;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentPerson;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentTitleSearches;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlDocumentTranslatedField;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlPerson;
import org.mrdlib.partnerContentManager.mediatum.MDLContent.MdlPersonDataQuality;

/**
 * Implementation of ContentConverter for partner mediaTUM.
 * mediaTUM offers a standardized OAI interface exhibiting data in the OAI Dublin Core format (http://www.openarchives.org/OAI/openarchivesprotocol.html).
 * 
 * @author wuestehube
 *
 */
public class MediaTUMContentConverter implements IContentConverter<OAIDCRecordConverted> {

	@Override
	public List<OAIDCRecordConverted> convertPartnerContentToStorablePartnerContent(String pathOfFileToConvert) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Converts one OAI DC record to MDL's document table. Checks provided data for plausibility.
	 * 
	 * @param oaidcRecord
	 * @return null if data is inplausible
	 */
	private MdlDocument MapMediaTumContentToMdlDocumentTable(OAIDCRecord oaidcRecord) {
		// check data for plausibility
		// title
		if (oaidcRecord.getTitles().size() != 1) {
			return null;
		}
		// publisher
		if (oaidcRecord.getPublishers().size() != 1) {
			return null;
		}
		// language
		if (oaidcRecord.getLanguages().size() != 1) {
			return null;
		}
		// year
		if (oaidcRecord.getDates().size() != 1) {
			return null;
		}
		
		long document_id = 0;	// no mapping
		String id_original = "";	// TODO: get all elements of oaidcRecord.getIdentifiers() as concatenated string
		long collection_id = 0;
		String title = oaidcRecord.getTitles().get(0);
		String title_clean = "";	// TODO: create method that extracts clean title from title
		String published_in = oaidcRecord.getPublishers().get(0);
		String language = oaidcRecord.getLanguages().get(0);
		int publication_year = 0;	// TODO: create method for extracting the year from OAIDC date format
		MdlDocumentType type = MdlDocumentType.BOOK;	// TODO: create method that maps mediaTUM's type to MDL type
		String keywords = "";	// TODO: get concatenation of elements of oaidcRecord.getSubjects() - possibly create method
		Date added = new Date();	// no mapping
		
		MdlDocument mdlDocument = new MdlDocument(document_id, id_original, collection_id, title, title_clean, published_in, language, publication_year, type, keywords, added);
		
		return mdlDocument;
	}
	
	private MdlDocumentAbstract MapMediaTumContentToMdlDocumentAbstractTable() {
		long document_abstract_id = 0;
		long document_id = 0;
		String language = "";
		String abstract_ = "";
		Date added = new Date();
		
		MdlDocumentAbstract mdlDocumentAbstract = new MdlDocumentAbstract(document_abstract_id, document_id, language, abstract_, added);
		
		return mdlDocumentAbstract;
	}

	private MdlDocumentExternalId MapMediaTumContentToMdlDocumentExternalIdTable() {
		long document_id = 0;
		MdlDocumentExternalIdExternalName external_name = MdlDocumentExternalIdExternalName.ARXIV;
		String external_id = "";
		
		MdlDocumentExternalId mdlDocumentExternalId = new MdlDocumentExternalId(document_id, external_name, external_id);
		
		return mdlDocumentExternalId;
	}
	
	private MdlDocumentKeyphrase MapMediaTumContentToMdlDocumentKeyphraseTable() {
		long doc_id = 0;
		String term = "";
		float score = 0;
		int gramity = 0;
		MdlDocumentKeyphraseSource source = MdlDocumentKeyphraseSource.ABSTRACT;
		
		MdlDocumentKeyphrase mdlDocumentKeyphrase = new MdlDocumentKeyphrase(doc_id, term, score, gramity, source);
		
		return mdlDocumentKeyphrase;
	}
	
	private MdlDocumentKeyphraseCount MapMediaTumContentToMdlDocumentKeyphraseCountTable() {
		long doc_id = 0;
		int gramity = 0;
		MdlDocumentKeyphraseSource source = MdlDocumentKeyphraseSource.ABSTRACT;
		long count = 0;
		
		MdlDocumentKeyphraseCount mdlDocumentKeyphraseCount = new MdlDocumentKeyphraseCount(doc_id, gramity, source, count);
		
		return mdlDocumentKeyphraseCount;
	}
	
	private MdlDocumentPerson MapMediaTumContentToMdlDocumentPersonTable() {
		long document_person_id = 0;
		long document_id = 0;
		long person_id = 0;
		int rank = 0;
		Date added = new Date();
		
		MdlDocumentPerson mdlDocumentPerson = new MdlDocumentPerson(document_person_id, document_id, person_id, rank, added);
		
		return mdlDocumentPerson;
	}
	
	private MdlDocumentTitleSearches MapMediaTumContentToMdlDocumentTitleSearchesTable() {
		long document_title_search_id = 0;
		String clean_search_string = "";
		String original_search_string = "";

		MdlDocumentTitleSearches mdlDocumentTitleSearches = new MdlDocumentTitleSearches(document_title_search_id, clean_search_string, original_search_string);
		
		return mdlDocumentTitleSearches;
	}
	
	private MdlDocumentTranslatedField MapMediaTumContentToDocumentTranslatedFieldTable() {
		long document_id = 0;
		MdlDocumentTranslatedFieldFieldType field_type = MdlDocumentTranslatedFieldFieldType.ABSTRACT;
		MdlDocumentTranslatedFieldTranslationTool translation_tool = MdlDocumentTranslatedFieldTranslationTool.JOSHUA;
		String source_language = "";
		String target_language = "";
		String text = "";
		
		MdlDocumentTranslatedField mdlDocumentTranslatedField = new MdlDocumentTranslatedField(document_id, field_type, translation_tool, source_language, target_language, text);
		
		return mdlDocumentTranslatedField;
	}
	
	private MdlPerson MapMediaTumContentToMdlPersonTable() {
		long person_id = 0;
		String name_first = "";
		String name_middle = "";
		String name_last = "";
		String name_unstructured = "";
		Date added = new Date();
		MdlPersonDataQuality data_quality = MdlPersonDataQuality.INVALID;
		
		MdlPerson mdlPerson = new MdlPerson(person_id, name_first, name_middle, name_last, name_unstructured, added, data_quality);

		return mdlPerson;
	}
	
}