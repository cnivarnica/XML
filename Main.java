import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        XMLProcessor processor = new XMLProcessor();

        System.out.print("Enter the XML file path: ");
        String filePath = scanner.nextLine();

        try {
            processor.openFile(filePath);
            System.out.println("XML file opened successfully.");
        } catch (IOException e) {
            System.out.println("Failed to open XML file: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\nAvailable operations:");
            System.out.println("1. Print");
            System.out.println("2. Select");
            System.out.println("3. Set");
            System.out.println("4. Children");
            System.out.println("5. Child");
            System.out.println("6. Text");
            System.out.println("7. Delete");
            System.out.println("8. Newchild");
            System.out.println("9. XPath");
            System.out.println("0. Exit");

            System.out.print("Enter operation number: ");
            int operation = scanner.nextInt();
            scanner.nextLine();

            switch (operation) {
                case 1:
                    processor.print();
                    break;
                case 2:
                    System.out.print("Enter the element ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter the attribute key: ");
                    String key = scanner.nextLine();
                    processor.select(id, key);
                    break;
                case 3:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    System.out.print("Enter the attribute key: ");
                    key = scanner.nextLine();
                    System.out.print("Enter the attribute value: ");
                    String value = scanner.nextLine();
                    processor.set(id, key, value);
                    break;
                case 4:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    processor.children(id);
                    break;
                case 5:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    System.out.print("Enter the child number: ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    processor.child(id, n);
                    break;
                case 6:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    processor.text(id);
                    break;
                case 7:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    System.out.print("Enter the attribute key: ");
                    key = scanner.nextLine();
                    processor.delete(id, key);
                    break;
                case 8:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    processor.newchild(id);
                    break;
                case 9:
                    System.out.print("Enter the element ID: ");
                    id = scanner.nextLine();
                    System.out.print("Enter the XPath expression: ");
                    String xpath = scanner.nextLine();
                    processor.xpath(id, xpath);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid operation number. Please try again.");
            }
        }
    }
}
