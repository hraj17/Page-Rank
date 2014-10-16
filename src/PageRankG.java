import java.io.*;
import java.util.*;

/**
 * Created by harishrajagopal on 10/6/14.
 */
public class PageRankG {
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

    public static Integer sillyCounter = 0;
    public static Integer sillyRun1 = 1;
    public static Integer sillyRun2 = 10;
    public static Integer sillyRun3 = 100;
    public static void main(String[] args) throws IOException {

        preprocess();
        //foreach page p in P
        //PR(p) = 1/N
        for(String link : P)
        {
            PR.put(link,Double.valueOf(1/N));
        }
        //
        // print();

        boolean flag = true;
        while(sillyCounter++ <sillyRun3)
        {
            Double sinkPR = Double.valueOf(0);
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

        // print();
        ValueComparator bvc =  new ValueComparator(PR);
        TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);

        sorted_map.putAll(PR);
        print(sorted_map);
    }



    public static void  preprocess() throws IOException {

        file = new File("/Users/harishrajagopal/IdeaProjects/PageRank/input");
        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);

        String line;
        while ((line = br.readLine()) != null) {
            // process the line.
            //System.out.println(line);

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


    public static void print( TreeMap<String,Double> map) throws IOException {
        //Writer writer = null;
        FileWriter fstream;
        BufferedWriter out = null;
        int counter = 50;
        try {
            fstream = new FileWriter("sillyRun1.txt");
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

