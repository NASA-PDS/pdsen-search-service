package gov.nasa.pds.data.pds3.parser;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import gov.nasa.pds.data.pds3.model.Pds3Mission;
import gov.nasa.pds.data.util.xml.XPathUtils;
import gov.nasa.pds.data.util.xml.XmlDomCrawler;


public class Pds3MissionParser
{
    private XPathExpression xLid;
    private XPathExpression xTitle;    
    private XPathExpression xAltTitle;
    private XPathExpression xAltId;
    
    private XPathExpression xName;
    private XPathExpression xDescr;
    private XPathExpression xObjective;

    private XPathExpression xStart;
    private XPathExpression xStop;
    
    
    public Pds3MissionParser() throws Exception
    {
        XPathFactory xpf = XPathFactory.newInstance();
        
        xLid = XPathUtils.compileXPath(xpf, "//Identification_Area/logical_identifier");
        xTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/title");
        xAltId = XPathUtils.compileXPath(xpf, "//Identification_Area/Alias_List/Alias/alternate_id");
        xAltTitle = XPathUtils.compileXPath(xpf, "//Identification_Area/Alias_List/Alias/alternate_title");

        xName = XPathUtils.compileXPath(xpf, "//Mission_PDS3/mission_name");
        xDescr = XPathUtils.compileXPath(xpf, "//Mission_PDS3/mission_desc");
        xObjective = XPathUtils.compileXPath(xpf, "//Mission_PDS3/mission_objectives_summary");

        xStart = XPathUtils.compileXPath(xpf, "//Mission_PDS3/mission_start_date");
        xStop = XPathUtils.compileXPath(xpf, "//Mission_PDS3/mission_stop_date");
    }
    
    
    public Pds3Mission parse(Document doc) throws Exception
    {
        Pds3Mission obj = new Pds3Mission();

        obj.lid = XPathUtils.getStringValue(doc, xLid);
        obj.title = XPathUtils.getStringValue(doc, xTitle);
        obj.altId = XPathUtils.getStringArray(doc, xAltId);
        obj.altTitle = XPathUtils.getStringArray(doc, xAltTitle);

        obj.name = XPathUtils.getStringValue(doc, xName);
        obj.description = XPathUtils.getStringValue(doc, xDescr);
        obj.objective = XPathUtils.getStringValue(doc, xObjective);

        obj.startDate = XPathUtils.getStringValue(doc, xStart);
        obj.stopDate = XPathUtils.getStringValue(doc, xStop);
        
        return obj;
    }
}
