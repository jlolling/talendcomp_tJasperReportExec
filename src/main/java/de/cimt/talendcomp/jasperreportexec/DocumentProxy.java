package de.cimt.talendcomp.jasperreportexec;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public class DocumentProxy implements Document {
	
	private File xmlFile = null;
	private Document delegate = null;
	
	public DocumentProxy(File xmlFile) {
		if (xmlFile == null) {
			throw new IllegalArgumentException("xmlFile cannot be null");
		}
		this.xmlFile = xmlFile;
	}
	
	private Document getDocument() {
		if (delegate == null) {
			try {
				delegate = JRXmlUtils.parse(xmlFile, false);
			} catch (JRException e) {
				throw new RuntimeException(e);
			}
		}
		return delegate;
	}

	@Override
	public String getNodeName() {
		return getDocument().getNodeName();
	}

	@Override
	public String getNodeValue() throws DOMException {
		return getDocument().getNodeValue();
	}

	@Override
	public void setNodeValue(String nodeValue) throws DOMException {
		getDocument().setNodeValue(nodeValue);
	}

	@Override
	public short getNodeType() {
		return getDocument().getNodeType();
	}

	@Override
	public Node getParentNode() {
		return getDocument().getParentNode();
	}

	@Override
	public NodeList getChildNodes() {
		return getDocument().getChildNodes();
	}

	@Override
	public Node getFirstChild() {
		return getDocument().getFirstChild();
	}

	@Override
	public Node getLastChild() {
		return getDocument().getLastChild();
	}

	@Override
	public Node getPreviousSibling() {
		return getDocument().getPreviousSibling();
	}

	@Override
	public Node getNextSibling() {
		return getDocument().getNextSibling();
	}

	@Override
	public NamedNodeMap getAttributes() {
		return getDocument().getAttributes();
	}

	@Override
	public Document getOwnerDocument() {
		return getDocument().getOwnerDocument();
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return getDocument().insertBefore(newChild, refChild);
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return getDocument().replaceChild(newChild, oldChild);
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		return getDocument().removeChild(oldChild);
	}

	@Override
	public Node appendChild(Node newChild) throws DOMException {
		return getDocument().appendChild(newChild);
	}

	@Override
	public boolean hasChildNodes() {
		return getDocument().hasChildNodes();
	}

	@Override
	public Node cloneNode(boolean deep) {
		return getDocument().cloneNode(deep);
	}

	@Override
	public void normalize() {
		getDocument().normalize();
	}

	@Override
	public boolean isSupported(String feature, String version) {
		return getDocument().isSupported(feature, version);
	}

	@Override
	public String getNamespaceURI() {
		return getDocument().getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return getDocument().getPrefix();
	}

	@Override
	public void setPrefix(String prefix) throws DOMException {
		getDocument().setPrefix(prefix);
	}

	@Override
	public String getLocalName() {
		return getDocument().getLocalName();
	}

	@Override
	public boolean hasAttributes() {
		return getDocument().hasAttributes();
	}

	@Override
	public String getBaseURI() {
		return getDocument().getBaseURI();
	}

	@Override
	public short compareDocumentPosition(Node other) throws DOMException {
		return getDocument().compareDocumentPosition(other);
	}

	@Override
	public String getTextContent() throws DOMException {
		return getDocument().getTextContent();
	}

	@Override
	public void setTextContent(String textContent) throws DOMException {
		getDocument().setTextContent(textContent);
	}

	@Override
	public boolean isSameNode(Node other) {
		return getDocument().isSameNode(other);
	}

	@Override
	public String lookupPrefix(String namespaceURI) {
		return getDocument().lookupPrefix(namespaceURI);
	}

	@Override
	public boolean isDefaultNamespace(String namespaceURI) {
		return getDocument().isDefaultNamespace(namespaceURI);
	}

	@Override
	public String lookupNamespaceURI(String prefix) {
		return getDocument().lookupNamespaceURI(prefix);
	}

	@Override
	public boolean isEqualNode(Node arg) {
		return getDocument().isEqualNode(arg);
	}

	@Override
	public Object getFeature(String feature, String version) {
		return getDocument().getFeature(feature, version);
	}

	@Override
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return getDocument().setUserData(key, data, handler);
	}

	@Override
	public Object getUserData(String key) {
		return getDocument().getUserData(key);
	}

	@Override
	public DocumentType getDoctype() {
		return getDocument().getDoctype();
	}

	@Override
	public DOMImplementation getImplementation() {
		return getDocument().getImplementation();
	}

	@Override
	public Element getDocumentElement() {
		return getDocument().getDocumentElement();
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		return getDocument().createElement(tagName);
	}

	@Override
	public DocumentFragment createDocumentFragment() {
		return getDocument().createDocumentFragment();
	}

	@Override
	public Text createTextNode(String data) {
		return getDocument().createTextNode(data);
	}

	@Override
	public Comment createComment(String data) {
		return getDocument().createComment(data);
	}

	@Override
	public CDATASection createCDATASection(String data) throws DOMException {
		return getDocument().createCDATASection(data);
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(String target,
			String data) throws DOMException {
		return getDocument().createProcessingInstruction(target, data);
	}

	@Override
	public Attr createAttribute(String name) throws DOMException {
		return getDocument().createAttribute(name);
	}

	@Override
	public EntityReference createEntityReference(String name)
			throws DOMException {
		return getDocument().createEntityReference(name);
	}

	@Override
	public NodeList getElementsByTagName(String tagname) {
		return getDocument().getElementsByTagName(tagname);
	}

	@Override
	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		return getDocument().importNode(importedNode, deep);
	}

	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName)
			throws DOMException {
		return getDocument().createElementNS(namespaceURI, qualifiedName);
	}

	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName)
			throws DOMException {
		return getDocument().createAttributeNS(namespaceURI, qualifiedName);
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		return getDocument().getElementsByTagNameNS(namespaceURI, localName);
	}

	@Override
	public Element getElementById(String elementId) {
		return getDocument().getElementById(elementId);
	}

	@Override
	public String getInputEncoding() {
		return getDocument().getInputEncoding();
	}

	@Override
	public String getXmlEncoding() {
		return getDocument().getXmlEncoding();
	}

	@Override
	public boolean getXmlStandalone() {
		return getDocument().getXmlStandalone();
	}

	@Override
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		getDocument().setXmlStandalone(xmlStandalone);
	}

	@Override
	public String getXmlVersion() {
		return getDocument().getXmlVersion();
	}

	@Override
	public void setXmlVersion(String xmlVersion) throws DOMException {
		getDocument().setXmlVersion(xmlVersion);
	}

	@Override
	public boolean getStrictErrorChecking() {
		return getDocument().getStrictErrorChecking();
	}

	@Override
	public void setStrictErrorChecking(boolean strictErrorChecking) {
		getDocument().setStrictErrorChecking(strictErrorChecking);
	}

	@Override
	public String getDocumentURI() {
		return getDocument().getDocumentURI();
	}

	@Override
	public void setDocumentURI(String documentURI) {
		getDocument().setDocumentURI(documentURI);
	}

	@Override
	public Node adoptNode(Node source) throws DOMException {
		return getDocument().adoptNode(source);
	}

	@Override
	public DOMConfiguration getDomConfig() {
		return getDocument().getDomConfig();
	}

	@Override
	public void normalizeDocument() {
		getDocument().normalizeDocument();
	}

	@Override
	public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
		return getDocument().renameNode(n, namespaceURI, qualifiedName);
	}
	
}
