import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLProcessor {
    private Map<String, String> elementsById;

    public XMLProcessor() {
        elementsById = new HashMap<>();
    }

    public void openFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        StringBuilder xmlBuilder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            xmlBuilder.append(line.trim());
        }
        reader.close();
        processXML(xmlBuilder.toString());
    }

    private void processXML(String xml) {
        int index = 0;
        while (index < xml.length()) {
            int openTagStartIndex = xml.indexOf('<', index);
            int openTagEndIndex = xml.indexOf('>', openTagStartIndex);
            int closeTagStartIndex = xml.indexOf("</", openTagEndIndex);
            int closeTagEndIndex = xml.indexOf('>', closeTagStartIndex);

            if (openTagStartIndex != -1 && openTagEndIndex != -1 && closeTagStartIndex != -1 && closeTagEndIndex != -1) {
                String openTag = xml.substring(openTagStartIndex, openTagEndIndex + 1);
                String closeTag = xml.substring(closeTagStartIndex, closeTagEndIndex + 1);

                String id = extractAttribute(openTag, "id");
                if (id==null) {
                    id = generateUniqueID();
                    openTag = openTag.substring(0, openTag.length() - 1) + " id=\"" + id + "\">";
                } else if (elementsById.containsKey(id)) {
                    id = generateUniqueID(id);
                    openTag = openTag.substring(0, openTag.length() - 1) + " id=\"" + id + "\">";
                }

                String element = openTag + closeTag;
                elementsById.put(id, element);

                index = closeTagEndIndex + 1;
            } else {
                break;
            }
        }
    }

    private String extractAttribute(String tag, String key) {
        int start = tag.indexOf("\"") + 1;
        int end = tag.indexOf("\"", start);
        while (start != -1 && end != -1) {
            String attribute = tag.substring(start, end);
            String[] keyValue = attribute.split("=");
            String currentKey = keyValue[0].trim();
            String value = keyValue[1].trim();
            if (currentKey.equals(key)) {
                return value;
            }
            start = tag.indexOf("\"", end + 1) + 1;
            end = tag.indexOf("\"", start);
        }
        return null;
    }


    private String generateUniqueID() {
        int count = 0;
        while (true) {
            String id = String.valueOf(count);
            if (!elementsById.containsValue(id)) {
                return id;
            }
            count++;
        }
    }

    private String generateUniqueID(String baseID) {
        int count = 1;
        while (true) {
            String id = baseID + "_" + count;
            if (!elementsById.containsValue(id)) {
                return id;
            }
            count++;
        }
    }

    private String extractOpenTag(String element) {
        int endIndex = element.indexOf('>');
        return element.substring(0, endIndex + 1);
    }

    private String extractCloseTag(String element) {
        int startIndex = element.lastIndexOf('<');
        return element.substring(startIndex);
    }

    private String extractContent(String element) {
        int startIndex = element.indexOf('>') + 1;
        int endIndex = element.lastIndexOf('<');
        return element.substring(startIndex, endIndex);
    }


    public void print() {
        for (String element : elementsById.values()) {
            printElement(element, 0);
        }
    }

    private void printElement(String element, int indentLevel) {
        String openTag = extractOpenTag(element);
        String closeTag = extractCloseTag(element);
        String content = extractContent(element);

        // Print indentation
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }

        // Print open tag
        System.out.println(openTag);

        // Print content if present
        if (!content.isEmpty()) {
            for (int i = 0; i <= indentLevel; i++) {
                System.out.print("  ");
            }
            System.out.println(content);
        }

        // Recursively print children
        List<String> children = getChildren(element);
        for (String child : children) {
            printElement(child, indentLevel + 1);
        }

        // Print indentation and close tag
        for (int i = 0; i < indentLevel; i++) {
            System.out.print("  ");
        }
        System.out.println(closeTag);
    }


    private String getOpenTag(String element) {
        int closingBracketIndex = element.indexOf(">");
        return element.substring(0, closingBracketIndex + 1);
    }

    private String getCloseTag(String element) {
        String tagName = getTagName(element);
        return "</" + tagName + ">";
    }

    private String getText(String element) {
        int openingBracketIndex = element.indexOf(">") + 1;
        int closingBracketIndex = element.lastIndexOf("<");
        return element.substring(openingBracketIndex, closingBracketIndex).trim();
    }

    private List<String> getChildren(String element) {
        String tagName = getTagName(element);
        String startTag = "<" + tagName;
        String endTag = "</" + tagName + ">";
        int startIndex = element.indexOf(startTag);
        int endIndex = element.lastIndexOf(endTag);
        if (startIndex == -1 || endIndex == -1) {
            return new ArrayList<>();
        }
        String childrenString = element.substring(startIndex + startTag.length(), endIndex);
        List<String> children = new ArrayList<>();
        Pattern pattern = Pattern.compile("<([^/][^>]*?)>");
        Matcher matcher = pattern.matcher(childrenString);
        while (matcher.find()) {
            children.add(matcher.group());
        }
        return children;
    }



    private String getTagName(String element) {
        int openingBracketIndex = element.indexOf("<") + 1;
        int closingBracketIndex = element.indexOf(" ");
        if (closingBracketIndex == -1) {
            closingBracketIndex = element.indexOf(">");
        }
        return element.substring(openingBracketIndex, closingBracketIndex);
    }

    private String getIndentation(int indentLevel) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indentation.append("  ");
        }
        return indentation.toString();
    }


    public void select(String id, String key) {
        String element = elementsById.get(id);
        if (element != null) {
            String value = extractAttributeValue(element, key);
            System.out.println(value);
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    private String extractAttributeValue(String element, String key) {
        int startIndex = element.indexOf(key + "=") + key.length() + 2;
        int endIndex = element.indexOf('"', startIndex);
        return element.substring(startIndex, endIndex);
    }

    public void set(String id, String key, String value) {
        String element = elementsById.get(id);
        if (element != null) {
            String newElement = element.replaceAll(key + "=\"[^\"]*\"", key + "=\"" + value + "\"");
            elementsById.put(id, newElement);
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void children(String id) {
        String element = elementsById.get(id);
        if (element != null) {
            String[] lines = element.split("\n");
            for (String line : lines) {
                if (line.startsWith("<") && !line.startsWith("</")) {
                    System.out.println(line);
                }
            }
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void child(String id, int n) {
        String element = elementsById.get(id);
        if (element != null) {
            String[] lines = element.split("\n");
            int count = 0;
            for (String line : lines) {
                if (line.startsWith("<") && !line.startsWith("</")) {
                    count++;
                    if (count == n) {
                        System.out.println(line);
                        return;
                    }
                }
            }
            System.out.println("Child number '" + n + "' not found for element with ID '" + id + "'.");
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void text(String id) {
        String element = elementsById.get(id);
        if (element != null) {
            int startIndex = element.indexOf('>') + 1;
            int endIndex = element.indexOf('<', startIndex);
            String text = element.substring(startIndex, endIndex).trim();
            System.out.println(text);
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void delete(String id, String key) {
        String element = elementsById.get(id);
        if (element != null) {
            String newElement = element.replaceAll(" " + key + "=\"[^\"]*\"", "");
            elementsById.put(id, newElement);
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void newchild(String id) {
        String element = elementsById.get(id);
        if (element != null) {
            String newID = generateUniqueID();
            String newChild = "<child id=\"" + newID + "\"/>";
            String newElement = element.replace("</", newChild + "</");
            elementsById.put(id, newElement);
        } else {
            System.out.println("Element with ID '" + id + "' not found.");
        }
    }

    public void xpath(String id, String xpath) {
        String[] tokens = xpath.split("/");
        String currentID = id;
        for (String token : tokens) {
            if (token.endsWith("]")) {
                int index = extractIndex(token);
                String elementName = extractElementName(token);
                int count = 0;
                for (String currentId : elementsById.keySet()) {
                    if (currentId.startsWith(currentID + "_")) {
                        String element = elementsById.get(currentId);
                        if (element.startsWith("<" + elementName)) {
                            count++;
                            if (count == index) {
                                currentID = currentId;
                                break;
                            }
                        }
                    }
                }
            } else {
                String elementName = token;
                int count = 0;
                for (String currentId : elementsById.keySet()) {
                    if (currentId.startsWith(currentID + "_")) {
                        String element = elementsById.get(currentId);
                        if (element.startsWith("<" + elementName)) {
                            count++;
                            currentID = currentId;
                            break;
                        }
                    }
                }
                if (count == 0) {
                    System.out.println("Element with name '" + elementName + "' not found under element with ID '" + currentID + "'.");
                    return;
                }
            }
        }
        String element = elementsById.get(currentID);
        if (element != null) {
            System.out.println(element);
        } else {
            System.out.println("Element with ID '" + currentID + "' not found.");
        }
    }

    private int extractIndex(String token) {
        int startIndex = token.indexOf('[') + 1;
        int endIndex = token.indexOf(']');
        String indexStr = token.substring(startIndex, endIndex);
        return Integer.parseInt(indexStr);
    }

    private String extractElementName(String token) {
        return token.substring(0, token.indexOf('['));
    }
}
