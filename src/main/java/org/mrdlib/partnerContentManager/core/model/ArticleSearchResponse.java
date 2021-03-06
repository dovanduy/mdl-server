/*
 * CORE API v2
 * <p style=\"text-align: justify;\">You can use the CORE API to access the      resources harvested and enriched by CORE. If you encounter any problems with the API, please <a href=\"/contact\">report them to us</a>.</p>  <h2>Overview</h2> <p style=\"text-align: justify;\">The API is organised by resource type. The resources are <b>articles</b>,      <b>journals</b> and <b>repositories</b> and are represented using JSON data format. Furthermore,      each resource has a list of methods. The API also provides two global methods for accessing all resources at once.</p>  <h2>Response format</h2> <p style=\"text-align: justify;\">Response for each query contains two fields: <b>status</b> and <b>data</b>.     In case of an error status, the data field is empty. The data field contains a single object     in case the request is for a specific identifier (e.g. CORE ID, CORE repository ID, etc.), or       contains a list of objects, for example for search queries. In case of batch requests, the response     is an array of objects, each of which contains its own <b>status</b> and <b>data</b> fields.     For search queries the response contains an additional field <b>totalHits</b>, which is the      total number of items which match the search criteria.</p>  <h2>Search query syntax</h2>  <p style=\"text-align: justify\">Complex search queries can be used in all of the API search methods.     The query can be a simple string or it can be built using terms and operators described in Elasticsearch     <a href=\"http://www.elastic.co/guide/en/elasticsearch/reference/1.4/query-dsl-query-string-query.html#query-string-syntax\">documentation</a>.     The usable field names are <strong>title</strong>, <strong>description</strong>, <strong>fullText</strong>,      <strong>authorsString</strong>, <strong>publisher</strong>, <strong>repositories.id</strong>, <strong>repositories.name</strong>,      <strong>doi</strong>, <strong>oai</strong>, <strong>identifiers</strong> (which is a list of article identifiers including OAI, URL, etc.), <strong>language.name</strong>      and <strong>year</strong>. Some example queries: </p>  <ul style=\"margin-left: 30px;\">     <li><p>title:psychology and language.name:English</p></li>     <li><p>repositories.id:86 AND year:2014</p></li>     <li><p>identifiers:\"oai:aura.abdn.ac.uk:2164/3837\" OR identifiers:\"oai:aura.abdn.ac.uk:2164/3843\"</p></li>     <li><p>doi:\"10.1186/1471-2458-6-309\"</p></li> </ul>  <h2>Sort order</h2>  <p style=\"text-align: justify;\">For search queries, the results are ordered by relevance score. For batch      requests, the results are retrieved in the order of the requests.</p>  <h2>Parameters</h2> <p style=\"text-align: justify;\">The API methods allow different parameters to be passed. Additionally, there is an API key parameter which is common to all API methods. For all API methods      the API key can be provided either as a query parameter or in the request header. If the API key      is not provided, the API will return HTTP 401 error. You can register for an API key <a href=\"/services#api\">here</a>.</p>  <h2>API methods</h2>
 *
 * OpenAPI spec version: 2.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package org.mrdlib.partnerContentManager.core.model;

import java.util.Objects;




import org.mrdlib.partnerContentManager.core.model.Article;
import java.util.ArrayList;
import java.util.List;

/**
 * ArticleSearchResponse
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-07-10T16:43:47.568+09:00")
public class ArticleSearchResponse {
  /**
   * Operation status
   */

  public static final String OK = "OK";
  public static final String NOT_FOUND = "Not found";
    
  public static final String TOO_MANY_QUERIES = "Too many queries";
    
  public static final String MISSING_PARAMETER = "Missing parameter";
    
  public static final String INVALID_PARAMETER = "Invalid parameter";
    
  public static final String PARAMETER_OUT_OF_BOUNDS = "Parameter out of bounds";

  private String status = null;


  private Integer totalHits = null;


  private List<Article> data = new ArrayList<Article>();

  public ArticleSearchResponse status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Operation status
   * @return status
  **/

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ArticleSearchResponse totalHits(Integer totalHits) {
    this.totalHits = totalHits;
    return this;
  }

   /**
   * Total number of articles matching the search criteria
   * @return totalHits
  **/

  public Integer getTotalHits() {
    return totalHits;
  }

  public void setTotalHits(Integer totalHits) {
    this.totalHits = totalHits;
  }

  public ArticleSearchResponse data(List<Article> data) {
    this.data = data;
    return this;
  }

  public ArticleSearchResponse addDataItem(Article dataItem) {
    this.data.add(dataItem);
    return this;
  }

   /**
   * Search results
   * @return data
  **/

  public List<Article> getData() {
    return data;
  }

  public void setData(List<Article> data) {
    this.data = data;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArticleSearchResponse articleSearchResponse = (ArticleSearchResponse) o;
    return Objects.equals(this.status, articleSearchResponse.status) &&
        Objects.equals(this.totalHits, articleSearchResponse.totalHits) &&
        Objects.equals(this.data, articleSearchResponse.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, totalHits, data);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArticleSearchResponse {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    totalHits: ").append(toIndentedString(totalHits)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

