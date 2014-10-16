import java.io.*;
import java.util.*;

/**
 * Created by harishrajagopal on 10/6/14.
 */
public class PageRank {
    // P is the set of all pages; |P| = N
    // S is the set of sink nodes, i.e., pages that have no out links
    // M(p) is the set (without duplicates) of pages that link to page p
    // L(q) is the number of out-links (without duplicates) from page q
    // d is the PageRank damping/teleportation factor; use d = 0.85 as is typical

    public static Set<String> P = new HashSet<String>();
    static Set<String> S;// = new HashSet<String>();
    static HashMap<String, String[]> M = new HashMap<String, String[]>();
    public static HashMap<String , Integer> L = new HashMap<String, Integer>();
    public static HashMap<String , Double> PR = new HashMap<String, Double>();
    public static HashMap<String , Double> newPR = new HashMap<String, Double>();
    static Double N;
    static File file;

    public static Double d= 0.85;
    public static Double prevPerplexity = Double.MAX_VALUE;
    public static Integer perplexityCount = 0;
    public static  boolean perplexityFlag = false;

    public static Integer sillyRun1 = 1;


    public static FileWriter fstream;
    public static BufferedWriter out = null;

    public static FileWriter fstream1;
    public static BufferedWriter out1 = null;

    public static void main(String[] args) throws IOException {

        preprocess();
        //foreach page p in P
        //PR(p) = 1/N
        for(String link : P)
        {
            PR.put(link,Double.valueOf(1/N));
        }
        //file to hold perplexity values
        fstream1 = new FileWriter("perplexity.txt");
        out1 = new BufferedWriter(fstream1);

        boolean flag = true;
        //while the difference in old and new perplexity does not merge
        while(perplixity())
        {
            Double sinkPR = Double.valueOf(0);
            //calculate total sink PR
            for(String p:S)
            {
                sinkPR+=PR.get(p);
            }
            for(String p:P)
            {
                newPR.put(p,(Double)(1-d)/N);
                newPR.put(p,(Double)(newPR.get(p) + (d*sinkPR/N)));
                for(String q: M.get(p))
                {
                    newPR.put(p,(Double)(newPR.get(p) + (d*PR.get(q)/L.get(q))));
                }
            }
            for(String p : P)
            {
                PR.put(p,newPR.get(p));
            }
        }
        out1.close();

        //write page ranks to file
        ValueComparator bvc =  new ValueComparator(PR);
        TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
        sorted_map.putAll(PR);
        print(sorted_map);
    }

    public static boolean perplixity() throws IOException {
        Double sum= 0.0;
        //Calculating shannon entropy
        for(String p : P)
        {
            Double x = PR.get(p);
            sum+= x*Math.log(x);
        }
        Double newPerplixity = Math.pow(2, -sum);
        if(perplexityCount ==sillyRun1)
            prevPerplexity=newPerplixity;
        //change in perplexity is less than 1 for at least four iterations
        if((Math.abs(newPerplixity-prevPerplexity)) <1) {
            if (perplexityFlag) {
                perplexityCount++;
            } else {
                perplexityFlag = true;
                perplexityCount = 1;
            }
        }
        else
        {
            perplexityFlag=false;
            perplexityCount=0;
        }
        prevPerplexity=newPerplixity;
        //change in perplexity is less than 1 for at least four iterations
        if(perplexityCount==4)
            return false;
        else{
            out1.write(String.valueOf(newPerplixity) + "\n");
            return true;
        }
    }

    public static void  preprocess() throws IOException {

        file = new File("/Users/harishrajagopal/IdeaProjects/PageRank/input");
        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) {
            // process the line.
            String[] feed = line.split(" ");
            String LHS = feed[0];
            String[] RHS = new String[feed.length-1];
            for(int i=0; i<feed.length;i++)
            {
                P.add(feed[i]);
                if(i!=0)
                {
                    RHS[i-1] = feed[i];
                    if(L.containsKey(feed[i]))
                    {
                        L.put(feed[i],L.get(feed[i]) +1);
                    }
                    else
                    {
                        L.put(feed[i],1);
                    }
                }
            }
            M.put(LHS,RHS);
        }
        N=Double.valueOf(P.size());

        S= new HashSet<String>(P);
        S.removeAll(L.keySet());
        br.close();
    }

    //write hashmap to file
    public static void print( TreeMap<String,Double> map) throws IOException {
        int counter = 50;
        try {
            fstream = new FileWriter("perplexityPR.txt");
            out = new BufferedWriter(fstream);
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                if(counter-- ==0) {
                    break;
                }
                    Map.Entry pairs = (Map.Entry) it.next();
                    out.write(String.valueOf(pairs) + "\n");
            }
        } catch (IOException ex) {
            // report
        }finally {
            out.close();
        }
    }

}

class ValueComparator implements Comparator<String> {

    Map<String, Double> base;
    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    //this comparator imposes orderings that are inconsistent with equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}