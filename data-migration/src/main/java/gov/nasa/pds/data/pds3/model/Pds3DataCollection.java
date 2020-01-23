package gov.nasa.pds.data.pds3.model;

import java.util.Set;

public class Pds3DataCollection
{
    public String lid;
    public String vid;
    public String datasetId;
    
    public String title;
    public String type;
    public String description;

    public Set<String> processingLevels;
    public Set<String> codmacLevels;
    public String[] scienceFacets;
    
    public String[] keywords;
    public String purpose;
    
    public String collectionType;
    
    // References
    public Set<String> investigationIds;
    public Set<String> instrumentHostIds;
    public Set<String> instrumentIds;
    public Set<String> targetNames;
    public Set<String> targetTypes;
}

