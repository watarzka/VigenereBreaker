import java.util.*;
import edu.duke.*;
import java.io.File;
public class VigenereBreaker {
    /*public method sliceString, which has three parameters—a String message,
     * representing the encrypted message, an integer whichSlice, 
     * indicating the index the slice should start from, 
     * and an integer totalSlices, indicating the length of the key.
     * This method returns a String consisting of every totalSlices-th character from message,
     * starting at the whichSlice-th character.*/
    public String sliceString(String message, int whichSlice, int totalSlices) {
        //REPLACE WITH YOUR CODE
        String sliceOfMessage="";
        for(int i=whichSlice; i<message.length(); i=i+totalSlices)
        {
            char c=message.charAt(i);
            
            sliceOfMessage=sliceOfMessage+c;
        }
        //System.out.println(sliceOfMessage);
        return sliceOfMessage;
    }
    //mytester
    public void breakVigenere()
    {
        
        FileResource fr=new FileResource();
        //Use the asString method to read the entire contents of the file into a String.
        String message = fr.asString();
        
        HashMap<String,HashSet<String>> allLanguages= new HashMap<String,HashSet<String>>();
        
        DirectoryResource dr= new DirectoryResource();
        
        for (File f : dr.selectedFiles()) {
            
            FileResource fresource= new FileResource(f);
            String fileName=f.getName();
            
            allLanguages.put(fileName,readDictionary(fresource));
            System.out.println("Slownik {"+fileName+"} dodany do hashmapy.");
            
        }
        System.out.println("Dodawanie wszystkich slownikow do hashmapy ZAKONCZONE!");
        
        breakForAllLangs(message,allLanguages);
        
    }
    /*Write the public method tryKeyLength, which takes three parameters—a String encrypted that represents the encrypted message,
     * an integer klength that represents the key length,
     * and a character mostCommon that indicates the most common character in the language of the message.
     * This method should make use of the CaesarCracker class, as well as the sliceString method,
     * to find the shift for each index in the key. 
     * You should fill in the key (which is an array of integers) and return it.
     * Test this method on the text file athens_keyflute.txt, 
     * which is a scene from A Midsummer Night’s Dream encrypted with the key “flute”, and make sure you get the key {5, 11, 20, 19, 4}.*/
    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        //WRITE YOUR CODE HERE
        for(int i=0; i<klength ; i++)
        {
            String slice=sliceString(encrypted,i,klength);
            CaesarCracker cCracker= new CaesarCracker(mostCommon);
            int currKey=cCracker.getKey(slice);
            key[i]=currKey;
        }
        return key;
    }
    /*write the public method readDictionary, which has one parameter—a FileResource fr.
     * This method should first make a new HashSet of Strings, 
     * then read each line in fr (which should contain exactly one word per line), 
     * convert that line to lowercase, and put that line into the HashSet that you created. 
     * The method should then return the HashSet representing the words in a dictionary. 
     * All the dictionary files, including the English dictionary file, are included in the starter program you download. 
     * They are inside the folder called ‘dictionaries’.*/
    public HashSet<String> readDictionary(FileResource fr)
    {
        //make a new HashSet of Strings
        HashSet<String> hashset= new HashSet<String> ();
        //for(String word : dm.split("\\W")){}
        for (String line : fr.lines()) {
            
            line=line.toLowerCase();
            hashset.add(line);
            
            
        }
        return hashset;
    }
    /*countWords, which has two parameters—a String message, and a HashSet of Strings dictionary.
     *This method should split the message into words (use .split(“\\W+”),
     *which returns a String array), iterate over those words,
     *and see how many of them are “real words”—that is, 
     *how many appear in the dictionary. Recall that the words in dictionary are lowercase. 
     *This method should return the integer count of how many valid words it found.*/
    public int countWords(String message,HashSet<String> dictionary)
    {
        int counts=0;
        int i=0;
        for(String word : message.split("\\W"))
        {   
            i=i+1;
            word=word.toLowerCase();
            if(dictionary.contains(word))
            {
                counts=counts+1;
            }
        }
        //System.out.println("Wszystkie slowa "i)
        return counts;
    }
    /*breakForLanguage, which has two parameters—a String encrypted, and a HashSet of Strings dictionary.
     * This method should try all key lengths from 1 to 100 (use your tryKeyLength method to try one particular key length) 
     * to obtain the best decryption for each key length in that range. 
     * For each key length, your method should decrypt the message (using VigenereCipher’s decrypt method as before),
     * and count how many of the “words” in it are real words in English, 
     * based on the dictionary passed in (use the countWords method you just wrote).
     * This method should figure out which decryption gives the largest count of real words,
     * and return that String decryption. Note that there is nothing special about 100; 
     * we will just give you messages with key lengths in the range 1–100. 
     * If you did not have this information, you could iterate all the way to encrypted.length(). 
     * Your program would just take a bit longer to run.*/
    public String breakForLanguage(String encrypted, HashSet<String> dictionary)
    {
        int maxNumOfWords=0;
        String decryption="";
        String usedKey="";
        char commonChar=mostCommonCharIn(dictionary);
        for(int i=1; i<100 ; i++)
        {
            int [] key=tryKeyLength(encrypted, i, commonChar);
            VigenereCipher vc= new VigenereCipher(key);
            
            String decryptedMessage=vc.decrypt(encrypted);
            int currNumOfWords=countWords(decryptedMessage,dictionary);
            if(currNumOfWords > maxNumOfWords)
            {
                maxNumOfWords=currNumOfWords;
                decryption=decryptedMessage;
                usedKey= Arrays.toString(key);
            }
            
        }
        
        //System.out.println("Uzyty klucz"+usedKey);
        
        return decryption;
    }
    /*write the public method mostCommonCharIn, which has one parameter—a HashSet of Strings dictionary.
     * This method should find out which character, of the letters in the English alphabet, appears most often in the words in dictionary. 
     * It should return this most commonly occurring character. 
     * Remember that you can iterate over a HashSet of Strings with a for-each style for loop.*/
    public char mostCommonCharIn(HashSet<String> dictionary)
    {
        //tworze sobie kolekcje w ktorej bede przechowywac pary: key=litera alfabetu, value=ilosc wystapien danej litery w slowniku
        HashMap<Character, Integer> counts= new HashMap<Character, Integer>();
        char maxC='a';
        for (String word : dictionary) {
            word=word.toLowerCase();
            for(int i=0; i< word.length() ; i++)
            {
                char c=word.charAt(i);
                if(!counts.containsKey(c))
                {
                    counts.put(c,1);
                }
                else 
                {
                    counts.put(c,counts.get(c)+1);
                }
            }
            // process each item in turn 
            int maxValue=0;
            
            for (Character s : counts.keySet()) {
                // process each key in turn 
                int value=counts.get(s);
                if(value > maxValue)
                {
                    maxValue=value;
                    maxC=s;
                }
            } 
            
        } 
        return maxC;
    }
    /*write the public method breakForAllLangs, which has two parameters—a String encrypted, and a HashMap, called languages,
     * mapping a String representing the name of a language to a HashSet of Strings containing the words in that language.
     * Try breaking the encryption for each language, and see which gives the best results!
     * Remember that you can iterate over the languages.keySet() to get the name of each language,
     * and then you can use .get() to look up the corresponding dictionary for that language. 
     * You will want to use the breakForLanguage and countWords methods that you already wrote to do most of the work 
     * (it is slightly inefficient to re-count the words here, but it is simpler, and the inefficiency is not significant).
     * You will want to print out the decrypted message as well as the language that you identified for the message.*/
    public void breakForAllLangs(String encrypted, HashMap<String,HashSet<String>> languages)
    {   
        int maxCountOfWords=0;
        String finalDecryption="";
        String finalNameOfLanguage="";
        for (String nameOfLanguage : languages.keySet()) {
            //use breakForLanguage, which returns dectryptoin in one language
            String currDecryption=breakForLanguage(encrypted, languages.get(nameOfLanguage));
            //how many words did it end up with?
            int currCountOfWords=countWords(currDecryption,languages.get(nameOfLanguage));
            if(currCountOfWords > maxCountOfWords)
            {
                maxCountOfWords=currCountOfWords;
                finalDecryption=currDecryption;
                finalNameOfLanguage=nameOfLanguage;
            }
            
            
        }
        //print out the decrypted message
        System.out.println("**final decryption**"+finalDecryption.substring(0,100)+"**final decryption**");
        //print out the language
        System.out.println("Jezyk zakodowanej wiadomosci: {"+finalNameOfLanguage+"}");
    }
    public void tester()
    {
        
    }
    
    
}
