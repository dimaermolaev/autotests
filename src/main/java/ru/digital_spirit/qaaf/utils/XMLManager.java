package ru.digital_spirit.qaaf.utils;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;

public class XMLManager {
    /**
     * Метод для парсинга файла содержащего XML
     * @param path - принимает на вход строку, содержащую путь к расположению файла
     * @return - возвращает объект типа Document
     */
    public static Document parseXMLFile(String path) {
        Path path1 = Path.of(path);
        System.out.println(path1.toAbsolutePath());
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return builder.parse(path1.toString());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод для парсинга стринга содержащего XML
     * @param xmlString - принимает на вход строку, содержащую XML-документ
     * @return - возвращает объект типа Document
     */
    public static Document parseXMLString(String xmlString) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод возвращающий XML в ввиде отформатированной для удобного чтения строки
     * @param xml - объект типа org.w3c.dom.Document содержащий XML
     * @return - возвращает строку с форматированным XML
     */
    public static String getPrettyXMLString (Document xml) {
        StringBuilder sb = new StringBuilder();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(new DOMSource(xml), result);
            sb.append(result.getWriter().toString());
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    /**
     * Метод для преобразования объекта Document в строку
     * @param xml - объект типа org.w3c.dom.Document содержащий XML
     * @return - строка с содрежимым XML
     */
    public static String convertXMLToString(Document xml) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StringWriter stringWriter = new StringWriter();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(xml), new StreamResult(stringWriter));
        } catch (RuntimeException | TransformerException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    /**
     * Метод для получения данных из строки содержащей XML по XPath
     * @param xmlString - строка, содержащая XML
     * @param xpath - путь к нужному тегу по XPath
     * @return - возвращает содержимое тега в виде строки
     */
    public static String getTagByXpath(String xmlString, String xpath) {
        xpath = xpath.replaceAll("\\[0\\]", "[1]");
        return Jsoup.parse(xmlString).selectXpath(xpath).text();
    }

    /**
     * Метод для получения данных из объекта Document по XPath
     * @param xml - объект типа org.w3c.dom.Document содержащий XML
     * @param xpath - путь к нужному тегу по XPath
     * @return - возвращает содержимое тега в виде строки
     */
    public static String getXPathValue(Document xml, String xpath) {
        if (xml == null) {
            throw new NullPointerException("Не передан объект XML-документа.\n" +
                    "Убедитесь, что передан корректный XML-документ.");
        }
        XPath xpathFactory = XPathFactory.newInstance().newXPath();
        try {
            xpath = xpath.replaceAll("\\[0\\]", "[1]");
            xpath = xpath.startsWith("/") ? xpath : "/"+xpath.replace('.', '/');
            return xpathFactory.compile(xpath).evaluate(xml);
        } catch (XPathExpressionException e) {
            System.out.println(("Не удалось найти тег по xpath: " + xpath));
            return "Тег не найден";
        }
    }

    /**
     * Метод для проверки, что по заданному XPath в объекте Document существует нужный тег
     * @param xml - объект типа org.w3c.dom.Document содержащий XML
     * @param xpath - путь к нужному тегу по XPath
     * @return - true/false
     */
    public static Boolean xmlContainsTag(Document xml, String xpath) {
        if (xml == null) {
            throw new NullPointerException("Не передан объект XML-документа.\n" +
                    "Убедитесь, что передан корректный XML-документ.");
        }
        XPath xpathFactory = XPathFactory.newInstance().newXPath();
        try {
            xpath = xpath.replaceAll("\\[0\\]", "[1]");
            xpath = xpath.startsWith("/") ? xpath : "/"+xpath.replace('.', '/');
            NodeList nl = (NodeList) xpathFactory.compile(xpath).evaluate(xml, XPathConstants.NODESET);
            return nl.getLength() > 0;
        } catch (XPathExpressionException e) {
            System.out.println(("Ошибка!\n" +
                                "Не удалось найти тег по xpath: " + xpath));
            return false;
        }
    }
}
