    package com.company;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.TreeMap;

    public class Main {
        public static void main(String[] args) {
            String text = " Dieser Text muss codiert werden";

            //Es wird die Frequenz von Symbole im Text gerechnet
            TreeMap<Character, Integer> frequencies = countFrequancy(text);

            //Eine Liste der Baumblätter wird erstellt
            ArrayList<CodeTreeNode> codeTreeNodes = new ArrayList<>();
            for (Character c : frequencies.keySet()) {
                codeTreeNodes.add(new CodeTreeNode(c, frequencies.get(c)));
            }

            //Ein  Baum wird mit Huffman Algorithmus aufgebaut
            CodeTreeNode tree = huffman(codeTreeNodes);

            //Es wird eine Tabelle der Präfixcode für codierte Symbollen mithilfe des Baums
            TreeMap<Character, String> codes = new TreeMap<>();
            for (Character c : frequencies.keySet()) {
                codes.put(c, tree.getCodeForCharacter(c, ""));
            }

            System.out.println("Tabelle von Präfixcode: " + codes.toString());

            // Text wird codiert. Symbolen werden mit entsprechende Code ersetzt
            StringBuilder encoded = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                encoded.append(codes.get(text.charAt(i)));
            }

            System.out.println("Textgröße: " + text.getBytes().length * 8 + " Bit");
            System.out.println("Codierte Textgröße ist: " + encoded.length() + " Bit");
            System.out.println("Bits von codierter Text : " + encoded);


            //Nur um zu überprüfen ob das Algorithmus richtig funktioniert. Codierter Text wird decodiert werden
            String decoded = huffmanDecode(encoded.toString(), tree);

            System.out.println("Decodiert: " + decoded);
        }


        //eine Funktion, die zählt, wie oft(frequence) jedes Symbol(Buchstabe, Punkt, Komma etc.) vorkommt
        private static TreeMap<Character, Integer> countFrequancy(String text) {
            TreeMap<Character, Integer> freqMap = new TreeMap<>();
            for (int i = 1; i < text.length(); i++) {
                Character c = text.charAt(i);
                Integer count = freqMap.get(c);
                freqMap.put(c, count != null ? count + 1 : 1);
            }
            return freqMap;
        }

        //Huffman Algorithmus Funktion
        private static CodeTreeNode huffman(ArrayList<CodeTreeNode> codeTreeNodes) {
            while (codeTreeNodes.size() > 1) {
                Collections.sort(codeTreeNodes); // Knoten werden bei "weight" einsortiert
                //Hier werden zwei Knoten mit dem kleinstes "weight" genommen
                CodeTreeNode left = codeTreeNodes.remove(codeTreeNodes.size() - 1);//Ein Knoten wird genommen und sofort durch "remove" Funktion gelöscht wird
                CodeTreeNode right = codeTreeNodes.remove(codeTreeNodes.size() - 1);

                //Ein "Zwischenknott" wird aufgebaut
                CodeTreeNode parent = new CodeTreeNode(null, right.weight + left.weight, left, right);
                codeTreeNodes.add(parent);//Ein "Zwischenknott" wird wieder in Array gebracht
            }
            return codeTreeNodes.get(0);
        }
        //Überprüfung
        private static String huffmanDecode(String encoded, CodeTreeNode tree) {
            StringBuilder decoded = new StringBuilder();

            CodeTreeNode node = tree;
            for (int i = 0; i < encoded.length(); i++) {
                node = encoded.charAt(i) == '0' ? node.left : node.right;
                if (node.content != null) {
                    decoded.append(node.content);
                    node = tree;
                }
            }
            return decoded.toString();
        }

        // eine Klasse zur "Darstellung" eines Codebaums
        private static class CodeTreeNode implements Comparable<CodeTreeNode> {

            Character content;
            //weight ist die Frequence fürs Blatt oder die Summe der Frequencen von "töchterliche Blätter" für das "Zwischenknott"
            int weight;
            //Binäre Baum hat zwei Branche(linke und rechte)
            CodeTreeNode left;
            CodeTreeNode right;

            //Konstruktor
            public CodeTreeNode(Character content, int weight) {
                this.content = content;
                this.weight = weight;
            }

            //Konstruktor
            public CodeTreeNode(Character content, int weight, CodeTreeNode left, CodeTreeNode right) {
                this.content = content;
                this.weight = weight;
                this.left = left;
                this.right = right;
            }
            @Override
            public int compareTo(CodeTreeNode o) {
                return o.weight - weight;
            }

            //Hier wird es ein Code fürs Symbol "genommen"
            //sogenannte Tiefensuche
            public String getCodeForCharacter(Character ch, String parentPath) {
                if (content == ch) {
                    return parentPath;
                } else {
                    if (left != null) {
                        String path = left.getCodeForCharacter(ch, parentPath + 0); //rekursive Aufruf
                        if (path != null) {
                            return path;
                        }
                    }
                    if (right != null) {
                        String path = right.getCodeForCharacter(ch, parentPath + 1); //+1, da wir rechts auf dem Baum "abbiegen"
                        if (path != null) {
                            return path;
                        }
                    }
                }
                return null;
            }
        }
    }
