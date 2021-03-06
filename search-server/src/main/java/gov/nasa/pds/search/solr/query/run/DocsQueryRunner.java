package gov.nasa.pds.search.solr.query.run;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import gov.nasa.pds.nlp.ner.NerToken;
import gov.nasa.pds.search.solr.query.SolrQueryUtils;
import gov.nasa.pds.search.solr.query.bld.DocsQueryBuilder;
import gov.nasa.pds.search.util.RequestParameters;
import gov.nasa.pds.solr.SolrManager;


public class DocsQueryRunner
{
    private static final String COLLECTION_DOCS = "docs";
    

    public static QueryResponse runDocsQuery(List<NerToken> tokens, 
            RequestParameters reqParams) throws Exception
    {
        // Build Solr query
        DocsQueryBuilder queryBuilder = new DocsQueryBuilder(tokens);
        SolrQuery query = queryBuilder.build();
        if(query == null) return null;

        // Set "start" and "rows"
        SolrQueryUtils.setPageInfo(query, reqParams);
        // Set field list "fl"
        SolrQueryUtils.setFieldList(query, reqParams);

        // Call Solr and get results
        SolrClient solrClient = SolrManager.getInstance().getSolrClient();
        QueryResponse resp = solrClient.query(COLLECTION_DOCS, query);

        return resp;
    }

}
