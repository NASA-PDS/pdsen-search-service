package gov.nasa.pds.data.pds3.tools;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import gov.nasa.pds.data.pds3.model.Pds3DataCollection;
import gov.nasa.pds.data.pds3.solr.ProductCollectionWriterPds3;
import gov.nasa.pds.data.util.FieldMap;
import gov.nasa.pds.data.util.xml.SolrDocParser;


public class SolrDumpProcessor_Pds3DataSet
{
    private static class CleanCB implements SolrDocParser.Callback
    {
        private FieldMap fields;
        
        private Pds3DataSetProcessor dsp;
        private ProductCollectionWriterPds3 writer;

        private Set<String> ignoreFields;
        private Set<String> doNotCleanText;

        public int counter;
        public String investigationFilter;
        
        
        public CleanCB(Pds3DataSetProcessor dsp, String outPath, String investigationFilter) throws Exception
        {
            this.dsp = dsp;
            this.writer = new ProductCollectionWriterPds3(outPath);
            this.investigationFilter = investigationFilter;
            
            // Ignore fields
            ignoreFields = new HashSet<>();
            
            ignoreFields.add("objectType");
            ignoreFields.add("data_product_type");
            ignoreFields.add("data_object_type");
            
            ignoreFields.add("modification_date");
            ignoreFields.add("modification_description");
            
            ignoreFields.add("investigation_start_date");
            ignoreFields.add("investigation_stop_date");

            ignoreFields.add("full_name");            
            
            ignoreFields.add("search_id");
            ignoreFields.add("timestamp");
            ignoreFields.add("score");
            
            ignoreFields.add("target_type");
            
            // Temporary
            ignoreFields.add("confidence_level_note");
            ignoreFields.add("archive_status");
            ignoreFields.add("external_reference_text");
            ignoreFields.add("pds_model_version");
            ignoreFields.add("citation_description");
            ignoreFields.add("resLocation");
            ignoreFields.add("resource_ref");
            
            ignoreFields.add("start_time");
            ignoreFields.add("stop_time");
            ignoreFields.add("data_set_release_date");

            
            // Text normalization
            doNotCleanText = new HashSet<>();
            //doNotCleanText.add("data_set_description");
            doNotCleanText.add("confidence_level_note");
        }

        
        public void close() throws Exception
        {
            writer.close();
        }
        
        @Override
        public void onDocStart()
        {
            fields = new FieldMap();
        }


        @Override
        public void onField(String name, String value)
        {
            // Ignore fields
            if(name.startsWith("form-")) return;
            if(ignoreFields.contains(name)) return;
            
            if(value.equalsIgnoreCase("null")) return;
            if(value.equalsIgnoreCase("unknown")) return;
            if(value.equalsIgnoreCase("unk")) return;
            if(value.equalsIgnoreCase("not applicable")) return;
            if(value.equalsIgnoreCase("n/a")) return;
            
            // Normalize spaces (remove end of lines and repeating spaces)
            if(!doNotCleanText.contains(name))
            {
                value = StringUtils.normalizeSpace(value);
            }
            
            // Replace investigation_name with investigation_id
            if(name.equals("investigation_name"))
            {
                String id = dsp.getInvestigationIdByName(value);
                if(id == null)
                {
                    System.out.println("WARNING: Unknown investigation name: " + value);                    
                }
                else
                {
                    String[] tokens = id.split(",");
                    for(String token: tokens)
                    {
                        fields.addValue("investigation_id", token.toLowerCase());
                    }
                }
                
                return;
            }
            
            fields.addValue(name, value);
        }
        
        
        @Override
        public boolean onDocEnd()
        {
            try
            {
                Set<String> investigationIds = fields.getValues("investigation_id"); 
                if(investigationIds == null) return true;
                if(investigationIds.size() == 1 && investigationIds.contains("ground_based")) return true;
                
                if(investigationFilter != null && !investigationIds.contains(investigationFilter)) return true;

                Pds3DataCollection data = dsp.process(fields);
                writer.write(data);
                
                counter++;
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
            
            return true;
        }
        
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////    

    private Pds3DataSetProcessor dsp;
    
    
    public SolrDumpProcessor_Pds3DataSet() throws Exception
    {
        dsp = new Pds3DataSetProcessor();
    }
        
    
    public void processFile(String inPath, String outPath) throws Exception
    {
        processFile(inPath, outPath, null);
    }
    
    public void processFile(String inPath, String outPath, String investigationFilter) throws Exception
    {
        CleanCB cb = new CleanCB(dsp, outPath, investigationFilter);
        SolrDocParser parser = new SolrDocParser(inPath, cb);
        parser.parse();
        parser.close();
        cb.close();
        
        System.out.println(cb.counter);
    }

}
