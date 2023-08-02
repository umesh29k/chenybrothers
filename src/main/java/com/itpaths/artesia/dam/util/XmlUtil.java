package com.itpaths.artesia.dam.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class XmlUtil {
    public void assetPropertiesDOM() {
        String fileName = "test.png";
        String systemId = "hybrissystemid";
        String productId = "";
        int indx = 0;
        try {
            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();


            dBuilder.getDOMImplementation();
            // root element
            Element rootElement = doc.createElement("TEAMS_ASSET_FILE");
            doc.appendChild(rootElement);

            // supercars element
            Element assets = doc.createElement("ASSETS");
            rootElement.appendChild(assets);

            Element asset = doc.createElement("ASSET");
            assets.appendChild(asset);
            Element metadata = doc.createElement("METADATA");
            asset.appendChild(metadata);
            Element uois = doc.createElement("UOIS");
            uois.setAttribute("IS_EDITABLE", "Y");
            uois.setAttribute("METADATA_STATE", "NORMAL");
            uois.setAttribute("MODEL_ID", "102");
            uois.setAttribute("CONTENT_STATE", "NORMAL");
            uois.setAttribute("CONTENT_STATE_USER_ID", "1001");
            uois.setAttribute("NAME", fileName);
            uois.setAttribute("IMPORT_USER_ID", "1001");
            metadata.appendChild(uois);
            Element hybris = doc.createElement("OPENTEXT_HYBRIS_PRODUCTS");
            hybris.setAttribute("PRODUCT_TAG_NAME", productId);
            hybris.setAttribute("PRODUCT_ATTRIBUTE_NAME", productId);
            hybris.setAttribute("PRODUCT_ID", productId);
            hybris.setAttribute("SYSTEM_ID", systemId);
            uois.appendChild(hybris);
            Element content = doc.createElement("CONTENT");
            asset.appendChild(content);
            Element master = doc.createElement("MASTER");
            master.setAttribute("FILES", "asset00" + indx);
            content.appendChild(master);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            DOMImplementation domImpl = doc.getImplementation();
            DocumentType doctype = domImpl.createDocumentType("doctype",
                    "-//TEAMS//DTD asset and link file//EN",
                    "Tasset.dtd");

            NamedNodeMap oldMap = doctype.getEntities();
            NamedNodeMap newMap = doctype.getEntities();
            int length = oldMap.getLength();
            for (int i = 0; i < length; ++i) {
                Entity oldEntity = (Entity) oldMap.item(i);
                oldEntity.setNodeValue("Test_NODE_VALUE");
                doctype.setNodeValue("[This is test]");
            }


            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("e:\\assetProperties.xml"));
            transformer.transform(source, result);
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (TransformerConfigurationException transformerConfigurationException) {
            transformerConfigurationException.printStackTrace();
        } catch (TransformerException transformerException) {
            transformerException.printStackTrace();
        } catch (ParserConfigurationException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        }
    }
}
