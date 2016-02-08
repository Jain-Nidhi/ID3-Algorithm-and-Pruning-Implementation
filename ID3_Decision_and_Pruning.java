
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import static jdk.nashorn.internal.objects.Global.print;

/**
 * @Author: Nidhi Jain
 * @Date: 02/05/2016
 * @Synopsis: Code to build a decision tree using ID3 algorithm
 *            and prune it by certain no. of nodes
 * @Input: Training data, Validation data, Test data, No. of nodes to prune,
 *         Display/Hide the final Tree
 */
public class ID3_Decision_and_Pruning {

    private String training_filename;
    private String validation_filename;
    private String test_filename;
    private int[][] training;
    private int[][] validation;
    private int[][] test;
    private int training_rows;
    private int training_cols;
    private int validation_rows;
    private int validation_cols;
    private int test_rows;
    private int test_cols;
    private int prune;
    private int flag;
    private ArrayList<String> decision_tree;
    private int tree_index;
    private int one_zero;
    private int arr[];
    String[] del_attributes;
    int remaining_attributes[];
    String del_condition[];
    int last;
    double accuracy;
    
    public static void main(String[] args) {
       
        ID3_Decision_and_Pruning obj = new ID3_Decision_and_Pruning();
        obj.decision_tree = new ArrayList<>();
        
        obj.prune = Integer.parseInt(args[0]);
        obj.training_filename = args[1];
        obj.validation_filename = args[2];
        obj.test_filename = args[3];
        obj.flag = Integer.parseInt(args[4]);
        
        try
        {
            //Creat a training_set array using training_set.csv
            FileReader fr = new FileReader(obj.training_filename);
            BufferedReader br = new BufferedReader(fr);
            
            String str="";
            if((str = br.readLine()) != null) obj.training_cols = str.split(",").length;
            while(br.readLine() != null) obj.training_rows++;
            obj.training_rows++;
            obj.training = new int[obj.training_rows][obj.training_cols];
            
            FileReader fr1 = new FileReader(obj.training_filename);
            BufferedReader br1 = new BufferedReader(fr1);
            br1.readLine();
            for(int i=0;i<obj.training_cols;i++) 
                {
                    obj.training[0][i]= i;
                }
            int j=0;
            while((str = br1.readLine()) != null) 
            {
                String[] splt = str.split(",");
                j++;
                for(int i=0;i<obj.training_cols;i++) 
                {
                    obj.training[j][i]= Integer.parseInt(splt[i]);
                }
            }
            
            br.close();
            fr.close();
            br1.close();
            fr1.close();
            
            //Creat a validation_set array using validation_set.csv
            FileReader fr2 = new FileReader(obj.validation_filename);
            BufferedReader br2 = new BufferedReader(fr2);
            
            if((str = br2.readLine()) != null) obj.validation_cols = str.split(",").length;
            while(br2.readLine() != null) obj.validation_rows++;
            obj.validation_rows++;
            obj.validation = new int[obj.validation_rows][obj.validation_cols];
            
            FileReader fr3 = new FileReader(obj.validation_filename);
            BufferedReader br3 = new BufferedReader(fr3);
            
            br3.readLine();
            for(int i=0;i<obj.validation_cols;i++) 
                {
                    obj.validation[0][i]= i;
                }
            j=0;
            while((str = br3.readLine()) != null) 
            {
                j++;
                String[] splt = str.split(",");
                for(int i=0;i<obj.validation_cols;i++) obj.validation[j][i]= Integer.parseInt(splt[i]);
            }
            
            br2.close();
            fr2.close();
            br3.close();
            fr3.close();
            
            //Creat a test_set array using test_set.csv
            FileReader fr4 = new FileReader(obj.test_filename);
            BufferedReader br4 = new BufferedReader(fr4);
            
            if((str = br4.readLine()) != null) obj.test_cols = str.split(",").length;
            while(br4.readLine() != null) obj.test_rows++;
            obj.test_rows++;
            obj.test = new int[obj.test_rows][obj.test_cols];
            
            FileReader fr5 = new FileReader(obj.test_filename);
            BufferedReader br5 = new BufferedReader(fr5);
            br5.readLine();
            for(int i=0;i<obj.test_cols;i++) 
                {
                    obj.test[0][i]= i;
                }
            j=0;
            while((str = br5.readLine()) != null) 
            {
                j++;
                String[] splt = str.split(",");
                for(int i=0;i<obj.test_cols;i++) obj.test[j][i]= Integer.parseInt(splt[i]);
             
            }
            
            br4.close();
            fr4.close();
            br5.close();
            fr5.close();
        }
        catch(FileNotFoundException ex){}
        catch(IOException ex){}
        
        obj.create_decision_tree();
        obj.label_leafs();
        obj.prune_decision_tree();
        if(obj.flag==1)
        {
            System.out.println("\nDecision Tree Model [After Pruning]");
            System.out.println("-----------------------------------");
            obj.display_result(0,"");
        }
        obj.accuracy_using_test();
    }
    
    public void create_decision_tree(){
        
        tree_index = -1;
        int attribute = -1; //Class field
        int parent = -4;
        int l_child = -1;
        int r_child = -1;
        int next_attribute = -1;
        remaining_attributes = new int[(int)Math.pow(2, training_cols+1)];
        String new_node="";
        arr= new int[(int)Math.pow(2, training_cols+1)];
        del_attributes= new String[(int)Math.pow(2, training_cols+1)];
        del_condition = new String[(int)Math.pow(2, training_cols+1)];
        
        //Root node creation
        
            next_attribute = find_root_attribute();
            if(next_attribute != -1)
            {
                String E1 = find_rootE1(next_attribute);
                String E0 = find_rootE0(next_attribute);
                
                if (E1.equals("+ve"))l_child = -2;
                else if (E1.equals("-ve"))l_child = -3;
                else if (E1.equals("open"))l_child = -1;
                
                if (E0.equals("+ve"))r_child = -2;
                else if (E0.equals("-ve"))r_child = -3;
                else if (E0.equals("open"))r_child = -1;
                
                
                tree_index++;
               
                arr[0]=tree_index;
                new_node = "0,"+next_attribute+","+l_child+","+r_child+","+parent;
                decision_tree.add(new_node);
                del_attributes[0] = ",9999,";
                del_condition[0] = ",9999,";
                
                remaining_attributes[0] = training_cols-2;
            }
            else return;
        
        last=0;
        
        while(last>-1)
        {
            int attr =0;
            attr=arr[0];
            String[] a = decision_tree.get(attr).split(",");
            attribute = Integer.parseInt(a[1]);
            
                if(Integer.parseInt(a[2])==-1)
                {
                    next_attribute = find_next_attribute(attribute,del_attributes[0]+attribute+",",del_condition[0]+"1,",remaining_attributes[0]);
                    if(next_attribute !=-1)
                    {
                        for(int z=0;z<training_cols-1;z++)
                        {
                            if(next_attribute==training[0][z])
                            {
                                next_attribute=z;
                                break;
                            }
                        }
                    last++;
                    del_attributes[last]= del_attributes[0]+attribute+",";
                    del_condition[last]= del_condition[0]+"1,";

                    String E1 = find_E1(next_attribute,del_attributes[last],del_condition[last]);
                    String E0 = find_E0(next_attribute,del_attributes[last],del_condition[last]);

                    if (E1.equals("+ve"))l_child = -2;
                    else if (E1.equals("-ve"))l_child = -3;
                    else if (E1.equals("open"))l_child = -1;

                    if (E0.equals("+ve"))r_child = -2;
                    else if (E0.equals("-ve"))r_child = -3;
                    else if (E0.equals("open"))r_child = -1;

                    tree_index++;

                    arr[last]=tree_index;

                    new_node = tree_index+","+next_attribute+","+l_child+","+r_child+","+attr;
                    decision_tree.add(new_node);
                    String[] b = decision_tree.get(attr).split(",");
                    String a1 = b[0]+","+b[1]+","+tree_index+","+b[3]+","+b[4];
                    decision_tree.set(attr, a1);
                    remaining_attributes[last] = remaining_attributes[0]-1;
                    }
                    
                }
                if(Integer.parseInt(a[3])==-1)
                {
                    next_attribute = find_next_attribute(attribute,del_attributes[0]+attribute+",",del_condition[0]+"0,",remaining_attributes[0]);
                    if(next_attribute !=-1)
                    {
                        for(int z=0;z<training_cols-1;z++)
                        {
                            if(next_attribute==training[0][z])
                            {
                                next_attribute=z;
                                break;
                            }
                        }
                    last++;
                    del_attributes[last]= del_attributes[0]+attribute+",";
                    del_condition[last]= del_condition[0]+"0,";

                    String E1 = find_E1(next_attribute,del_attributes[last],del_condition[last]);
                    String E0 = find_E0(next_attribute,del_attributes[last],del_condition[last]);

                    if (E1.equals("+ve"))l_child = -2;
                    else if (E1.equals("-ve"))l_child = -3;
                    else if (E1.equals("open"))l_child = -1;

                    if (E0.equals("+ve"))r_child = -2;
                    else if (E0.equals("-ve"))r_child = -3;
                    else if (E0.equals("open"))r_child = -1;

                    tree_index++;

                    arr[last]=tree_index;

                    new_node = tree_index+","+next_attribute+","+l_child+","+r_child+","+attr;
                    decision_tree.add(new_node);
                    String[] b = decision_tree.get(attr).split(",");
                    String a1 = b[0]+","+b[1]+","+b[2]+","+tree_index+","+b[4];
                    decision_tree.set(attr, a1);
                    remaining_attributes[last] = remaining_attributes[0]-1;
                    }
                }
            
            pop_first_element();
    }
}
   
    public void pop_first_element(){
        for(int i=0;i<last;i++)
        {
            arr[i]=arr[i+1];
            del_attributes[i]=del_attributes[i+1];
            del_condition[i]=del_condition[i+1];
            remaining_attributes[i]=remaining_attributes[i+1];
        }
        last--;
    }
    public int find_root_attribute(){
        int sum=0;
        for(int i=1;i<training_rows;i++)sum+=training[i][training_cols-1];
        double a=0,b=0,IE=0;
        a= (sum*1.0)/((training_rows-1)*1.0);
        b = (1.0*((training_rows-1)-sum))/((training_rows-1)*1.0);
        IE =(-1*(a)*(Math.log(a)/Math.log(2)))+(-1*(b)*(Math.log(b)/Math.log(2)));
        
        int num1=0,num0=0,one_yes=0,one_no=0;
        int zero_yes=0,zero_no=0;
        double max=0;int pos=-1;
        
        for(int i=0;i<training_cols-1;i++)
        {
           num1=0;num0=0;one_yes=0;one_no=0;zero_yes=0;zero_no=0;double Ione =0,Izero=0,IEc=0;
           for(int j=1;j<training_rows;j++)
           {
               if(training[j][i]==1 && training[j][training_cols-1]==1) one_yes++;
               else if(training[j][i]==1 && training[j][training_cols-1]==0) one_no++;
               else if(training[j][i]==0 && training[j][training_cols-1]==1) zero_yes++;
               else if(training[j][i]==0 && training[j][training_cols-1]==0) zero_no++;
               
               if(training[j][i]==1) num1++;
               else num0++;
           }
           
           Ione = (-1*((1.0*one_yes)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_yes)/((1.0)*(one_yes+one_no)))/Math.log(2)))+(-1*((1.0*one_no)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_no)/((1.0)*(one_yes+one_no)))/Math.log(2)));
           Izero = (-1*((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))/Math.log(2)))+(-1*((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))/Math.log(2)));
           
           IEc = IE - (((((1.0)*num1)/((1.0)*(num1+num0)))*Ione) +((((1.0)*num0)/((1.0)*(num1+num0)))*Izero));
           
         //  System.out.println("col: "+i+" IEC: "+IEc);
           if(IEc>max)
           {
          //     System.out.println("col: "+i+" IEC: "+IEc+" max:"+max);
               max = IEc;
               pos = i;
           }
        }
        
        return pos;
    }
    
    
    public String table_rows(String deleted_attributes,String delete_condition){
        int[][] temp = new int[training_rows][training_cols];
        String[] att = deleted_attributes.split(",");
        String[] val = delete_condition.split(",");
        int flg=0,k=-1;
        for(int i=0;i<training_rows;i++)
        {
            flg=0;
            for(int h=0;h<training_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && training[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<training_cols-1;h++)
            temp[k][h]= training[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[training_rows][training_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<training_cols-1;h++)
               {
                   if(!deleted_attributes.contains(","+h+","))
                   {
                       c++;
                       temp1[j][c]=temp[j][h];
                   }
               }
           }
        
        int sum=0;
        for(int i=1;i<=k;i++)sum+=temp1[i][c];
        
        if(sum==k) return"pure";
        else return "impure";
    }
    public int find_next_attribute(int attribute, String deleted_attributes, String delete_condition, int remaining){
       
        String chk = table_rows(deleted_attributes, delete_condition);
        if(chk.equals("pure") || remaining==0)
        {
            System.out.println("i am pure now");
            return -1;
        }
        
        int[][] temp = new int[training_rows][training_cols];
        String[] att = deleted_attributes.split(",");
        String[] val = delete_condition.split(",");
        int flg=0,k=-1;
        for(int i=0;i<training_rows;i++)
        {
            flg=0;
            for(int h=0;h<training_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && training[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<training_cols-1;h++)
            temp[k][h]= training[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[training_rows][training_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<training_cols-1;h++)
               {
                   if(!deleted_attributes.contains(","+h+","))
                   {
                       c++;
                       temp1[j][c]=temp[j][h];
                   }
               }
           }
        
        int sum=0;
        for(int i=1;i<=k;i++)sum+=temp1[i][c];
        double a = (1.0*sum)/((1.0)*(k));
        double b = ((1.0)*((k)-sum))/((1.0)*(k));
        double IE= (-1*(a)*(Math.log(a)/Math.log(2)))+(-1*(b)*(Math.log(b)/Math.log(2)));
        
        int num1=0,num0=0,one_yes=0,one_no=0;
        int zero_yes=0,zero_no=0;
        double max=0;int pos=-1;
        
        for(int i=0;i<=c;i++)
        {
           num1=0;num0=0;one_yes=0;one_no=0;zero_yes=0;zero_no=0;
           for(int j=1;j<=k;j++)
           {
               if(temp1[j][i]==1 && temp1[j][c]==1) one_yes++;
               else if(temp1[j][i]==1 && temp1[j][c]==0) one_no++;
               else if(temp1[j][i]==0 && temp1[j][c]==1) zero_yes++;
               else if(temp1[j][i]==0 && temp1[j][c]==0) zero_no++;
               
               if(temp1[j][i]==1) num1++;
               else num0++;
           }
          double Ione = (-1*((1.0*one_yes)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_yes)/((1.0)*(one_yes+one_no)))/Math.log(2)))+(-1*((1.0*one_no)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_no)/((1.0)*(one_yes+one_no)))/Math.log(2)));
          double Izero = (-1*((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))/Math.log(2)))+(-1*((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))/Math.log(2)));
          double IEc= IE - ((((1.0)*num1)/((1.0)*(num1+num0)))*Ione + (((1.0)*num0)/((1.0)*(num1+num0)))*Izero);
          double total=((((1.0)*num1)/((1.0)*(num1+num0)))*Ione + (((1.0)*num0)/((1.0)*(num1+num0)))*Izero);
         // if(remaining==1) System.out.println("num1: "+num1+" ,num0: "+num0+" ,zero_yes: "+zero_yes+" ,zero_no: "+zero_no+" ,total: "+total);
          
          if(IEc>max)
           {
           //    if(remaining==1) System.out.println("IEc>max, "+IEc);
               max = IEc;
               pos = i;
           }
        }
        if(pos==-1) return pos;
        else
        return temp1[0][pos];
    }
    public String find_E1(int next_attribute,String del_attributes,String del_condition){
        
        int[][] temp = new int[training_rows][training_cols];
        
        String[] att = del_attributes.split(",");
        String[] val = del_condition.split(",");
        int flg=0,k=-1;
        for(int i=0;i<training_rows;i++)
        {
            flg=0;
            for(int h=0;h<training_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && training[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<training_cols-1;h++)
            temp[k][h]= training[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[training_rows][training_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<training_cols-1;h++)
               {
                   if(!del_attributes.contains(","+h+","))
                   {
                       c++;
                       temp1[j][c]=temp[j][h];
                   }
               }
           }
           
        int one_yes=0,one_no=0;
        
        for(int j=1;j<=k;j++)
           {
               if(training[j][next_attribute]==1 && training[j][training_cols-1]==1) one_yes++;
               else if(training[j][next_attribute]==1 && training[j][training_cols-1]==0) one_no++;
           }
           double Ione = (-1*((1.0*one_yes)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_yes)/((1.0)*(one_yes+one_no)))/Math.log(2)))+(-1*((1.0*one_no)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_no)/((1.0)*(one_yes+one_no)))/Math.log(2)));
           
           if(Ione>0) return "open";
           else if(one_yes>one_no)return "+ve";
           else return "-ve";
    }
    public String find_E0(int next_attribute,String del_attributes,String del_condition){
        int[][] temp = new int[training_rows][training_cols];
        
        String[] att = del_attributes.split(",");
        String[] val = del_condition.split(",");
        int flg=0,k=-1;
        for(int i=0;i<training_rows;i++)
        {
            flg=0;
            for(int h=0;h<training_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && training[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<training_cols-1;h++)
            temp[k][h]= training[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[training_rows][training_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<training_cols-1;h++)
               {
                   if(!del_attributes.contains(","+h+","))
                   {
                       c++;
                       temp1[j][c]=temp[j][h];
                   }
               }
           }
           
        int zero_yes=0,zero_no=0;
        
        for(int j=1;j<=k;j++)
           {
               if(training[j][next_attribute]==0 && training[j][training_cols-1]==1) zero_yes++;
               else if(training[j][next_attribute]==0 && training[j][training_cols-1]==0) zero_no++;
           }
           double Izero = (-1*((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))/Math.log(2)))+(-1*((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))/Math.log(2)));
          
           if(Izero>0) return "open";
           else if(zero_yes>zero_no)return "+ve";
           else return "-ve";
    }
    public String find_rootE1(int attribute){
        int one_yes=0,one_no=0;
           for(int j=1;j<training_rows;j++)
           {
               if(training[j][attribute]==1 && training[j][training_cols-2]==1) one_yes++;
               else if(training[j][attribute]==1 && training[j][training_cols-2]==0) one_no++;
           }
           double Ione = (-1*((1.0*one_yes)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_yes)/((1.0)*(one_yes+one_no)))/Math.log(2)))+(-1*((1.0*one_no)/((1.0)*(one_yes+one_no)))*(Math.log((1.0*one_no)/((1.0)*(one_yes+one_no)))/Math.log(2)));
           
           if(Ione>0) return "open";
           else if(one_yes>one_no)return "+ve";
           else return "-ve";
    }
    public String find_rootE0(int attribute){
        int zero_yes=0,zero_no=0;
           for(int j=1;j<training_rows;j++)
           {
               if(training[j][attribute]==0 && training[j][training_cols-2]==1) zero_yes++;
               else if(training[j][attribute]==0 && training[j][training_cols-2]==0) zero_no++;
           }
           double Izero = (-1*((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_yes)/((1.0)*(zero_yes+zero_no)))/Math.log(2)))+(-1*((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))*(Math.log((1.0*zero_no)/((1.0)*(zero_yes+zero_no)))/Math.log(2)));
          
           if(Izero>0) return "open";
           else if(zero_yes>zero_no)return "+ve";
           else return "-ve";
    }
   
    
    public void label_leafs(){
        for(int i=decision_tree.size()-1;i>=0;i--)
        {
            String[] splt=decision_tree.get(i).split(",");
            int l=Integer.parseInt(splt[2]);
            int r=Integer.parseInt(splt[3]);
            int pos1=0,pos2=0;
            
            if(l==-1)
            {
                String[] spl=decision_tree.get(i).split(",");
                String clas=tree(Integer.parseInt(spl[0]),1);
                if(clas.equals("+ve"))spl[2]="-2";
                else spl[2]="-3";
                decision_tree.set(Integer.parseInt(spl[0]),spl[0]+","+spl[1]+","+spl[2]+","+spl[3]+","+spl[4]);
                pos1=Integer.parseInt(spl[2]);
            }
            if(r==-1)
            {
                String[] spl=decision_tree.get(i).split(",");
                String clas=tree(Integer.parseInt(spl[0]),0);
                if(clas.equals("+ve"))spl[3]="-2";
                else spl[3]="-3";
                decision_tree.set(Integer.parseInt(spl[0]),spl[0]+","+spl[1]+","+spl[2]+","+spl[3]+","+spl[4]);
                pos2=Integer.parseInt(spl[3]);
            }
            
            if(pos1==pos2 && (pos1==-2 || pos1==-3))
            {
                String[] spl=decision_tree.get(i).split(",");
                String[] parent=decision_tree.get(Integer.parseInt(spl[4])).split(",");
                if(spl[0].equals(parent[2]))
                {
                    if(pos1==-2) parent[2]="-2";
                    else parent[2]="-3";
                }
                else
                {
                    if(pos1==-2) parent[3]="-2";
                    else parent[3]="-3";
                }
                decision_tree.set(Integer.parseInt(parent[0]),parent[0]+","+parent[1]+","+parent[2]+","+parent[3]+","+parent[4]);
                spl[4]="-4";
                decision_tree.set(Integer.parseInt(spl[0]),spl[0]+","+spl[1]+","+spl[2]+","+spl[3]+","+spl[4]);
            }
            
        }
    }
    public String tree(int index,int mode){
        
        String del_attributes=",";
        String del_condition=",";
        String[] item = decision_tree.get(index).split(",");
        int ind=Integer.parseInt(item[1]);
        int flg=0;
        while(flg==0)
        {
            String[] parent= decision_tree.get(Integer.parseInt(item[4])).split(",");
            del_attributes+=parent[1]+",";
            if(item[0].equals(parent[2]))del_condition+="1,";
            else if(item[0].equals(parent[3]))del_condition+="0,";
            item=parent;
            if(parent[0].equals("0")) flg=1;
        }
        
        int[][] temp = new int[training_rows][training_cols];
        
        String[] att = del_attributes.split(",");
        String[] val = del_condition.split(",");
        flg=0;int k=-1;
        for(int i=0;i<training_rows;i++)
        {
            flg=0;
            for(int h=0;h<training_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && training[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<training_cols-1;h++)
            temp[k][h]= training[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[training_rows][training_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<training_cols-1;h++)
               {
                   if(!del_attributes.contains(","+h+","))
                   {
                       if(h==ind || h==training_cols-2)
                       {
                           c++;
                           temp1[j][c]=temp[j][h];
                       }
                   }
               }
           }
        int one_yes=0,one_no=0,zero_yes=0,zero_no=0;
        for(int j=1;j<=k;j++)
                {
                    if(temp1[j][0]==1 && temp1[j][1]==1) one_yes++;
                    else if(temp1[j][0]==1 && temp1[j][1]==0) one_no++;
                    else if(temp1[j][0]==0 && temp1[j][1]==1) zero_yes++;
                    else if(temp1[j][0]==0 && temp1[j][1]==0) zero_no++;
                }
        if(mode==1)
        {
            if(one_yes>=one_no)return "+ve";
            else return "-ve";
        }
        else
        {
            if(zero_yes>=zero_no)return "+ve";
            else return "-ve";
        }
    }
    public void display_result(int index,String str1){
       /* for(int i=0;i<decision_tree.size();i++)
        {
            String[] splt = decision_tree.get(i).split(",");
            System.out.println(splt[0]+","+splt[1]+","+splt[2]+","+splt[3]+","+splt[4]);
        }*/
       int ind=index;
       String str=str1;
       String[] splt = decision_tree.get(ind).split(",");
       String field= get_field(Integer.parseInt(splt[1]));
       
       if(splt[2].equals("-2"))System.out.println(str+field+" = 1 : 1");
       else if(splt[2].equals("-3"))System.out.println(str+field+" = 1 : 0");
       else 
       {
           System.out.println(str+field+" = 1 :");
           int l_child=Integer.parseInt(splt[2]);
           display_result(l_child,str+"| ");
       }
       
       if(splt[3].equals("-2"))System.out.println(str+field+" = 0 : 1");
       else if(splt[3].equals("-3"))System.out.println(str+field+" = 0 : 0");
       else 
       {
           System.out.println(str+field+" = 0 :");
           int r_child=Integer.parseInt(splt[3]);
           display_result(r_child,str+"| ");
       }
       
    }
    
    public String get_field(int i){
        switch (i){
            case 0:return "XB";
            case 1:return "XC";
            case 2:return "XD";
            case 3:return "XE";
            case 4:return "XF";
            case 5:return "XG";
            case 6:return "XH";
            case 7:return "XI";
            case 8:return "XJ";
            case 9:return "XK";
            case 10:return "XL";
            case 11:return "XM";
            case 12:return "XN";
            case 13:return "XO";
            case 14:return "XP";
            case 15:return "XQ";
            case 16:return "XR";
            case 17:return "XS";
            case 18:return "XT";
            case 19:return "XU";
            default: return "";
        }
    }
    
    public void prune_decision_tree(){
        if(prune==0 || prune>=decision_tree.size()) return;
        double max=0.0;String nd="";
        max = accuracy_using_validation(decision_tree);
        ArrayList<String> new_dec = new ArrayList<>();
        new_dec=decision_tree;
        ArrayList<String> new_dec1 = new ArrayList<>();
        new_dec1=decision_tree;
        
        int array1[]=new int[decision_tree.size()-1];
        for(int i=0;i<decision_tree.size()-1;i++) array1[i]=Integer.parseInt(decision_tree.get(i+1).split(",")[0]);
        int N = array1.length;
        
        if(prune > N)return;
        int found=0;
        
                while(found==0)
                {
                    int nodes[] = new int[prune];
                    for(int i=0;i<prune;i++)
                    {
                        Random rand = new Random();
                        int flg=0;
                        while(flg==0)
                        {
                            int r = rand.nextInt(decision_tree.size()-1);
                            int j=0;
                            for(j=0;j<=i;j++)if(nodes[j]==array1[r]) break;
                            if(j==(i+1))
                            {
                                nodes[i]=array1[r];
                                flg=1;
                            }
                        }
                    }
                    
                    new_dec = prune(nodes);
                    double acc = accuracy_using_validation(new_dec);
                    if(acc>max) 
                    {
                        max = acc;
                        new_dec1 = new_dec;
                        for(int i=0;i<nodes.length;i++)
                        nd+="["+nodes[i]+"]";
                        break;
                    }
                }
          
        decision_tree = new_dec1;
        System.out.println("\nPruned nodes: "+nd);
    }
    
    public ArrayList<String> prune(int[] nodes){
        ArrayList<String> new_dec = new ArrayList<>();
        new_dec = decision_tree;
        for(int j=0;j<nodes.length;j++)
            {
                String[] splt = new_dec.get(nodes[j]).split(",");
                int l=Integer.parseInt(splt[2]);
                int r=Integer.parseInt(splt[3]);
                int p=Integer.parseInt(splt[4]);
                if(p==-4) continue;
                if(l!=-2 && l!=-3)
                {
                    String[] c=new_dec.get(l).split(",");
                    c[4]="-4";
                    new_dec.set(l,c[0]+","+c[1]+","+c[2]+","+c[3]+","+c[4]);
                    String chk = chk_class(new_dec,nodes[j],1);
                    String[] splt1 = new_dec.get(nodes[j]).split(",");
                    if(chk.equals("+ve")) splt1[2]="-2";
                    else splt1[2]="-3";
                    new_dec.set(nodes[j], splt1[0]+","+splt1[1]+","+splt1[2]+","+splt1[3]+","+splt1[4]);
                }
                if(r!=-2 && r!=-3)
                {
                    String[] c=new_dec.get(r).split(",");
                    c[4]="-4";
                    new_dec.set(r,c[0]+","+c[1]+","+c[2]+","+c[3]+","+c[4]);
                    
                    String chk = chk_class(new_dec,nodes[j],0);
                    String[] splt1 = new_dec.get(nodes[j]).split(",");
                    if(chk.equals("+ve")) splt1[3]="-2";
                    else splt1[3]="-3";
                    new_dec.set(nodes[j], splt1[0]+","+splt1[1]+","+splt1[2]+","+splt1[3]+","+splt1[4]);
                }
                
            }
        
        return new_dec;
            
    }
    
    public String chk_class(ArrayList<String> new_dec, int index, int mode){
        String del_attributes=",";
        String del_condition=",";
        String[] item = new_dec.get(index).split(",");
        int ind=Integer.parseInt(item[1]);
        int flg=0;
        while(flg==0)
        {
            int k=Integer.parseInt(item[4]);
            if(k==-4) break;
            String[] parent= new_dec.get(k).split(",");
            del_attributes+=parent[1]+",";
            if(item[0].equals(parent[2]))del_condition+="1,";
            else if(item[0].equals(parent[3]))del_condition+="0,";
            item=parent;
            if(parent[0].equals("0")) flg=1;
        }
        
        int[][] temp = new int[validation_rows][validation_cols];
        
        String[] att = del_attributes.split(",");
        String[] val = del_condition.split(",");
        flg=0;int k=-1;
        for(int i=0;i<validation_rows;i++)
        {
            flg=0;
            for(int h=0;h<validation_cols-1;h++)
            {
                for(int j=1;j<att.length;j++)
                {
                    if(h==Integer.parseInt(att[j]) && validation[i][h]!=Integer.parseInt(val[j]))
                    {
                        if(i!=0)
                        {
                            flg=1;
                            break;
                        }
                    }
                }
                if(flg==1) break;
            }
            if(flg==1) continue;
            k++;
            for(int h=0;h<validation_cols-1;h++)
            temp[k][h]= validation[i][h];
        }
        
        int c=-1;
        int[][] temp1 = new int[validation_rows][validation_cols];
        for(int j=0;j<=k;j++)
           {
               c=-1;
               for(int h=0;h<validation_cols-1;h++)
               {
                   if(!del_attributes.contains(","+h+","))
                   {
                       if(h==ind || h==validation_cols-2)
                       {
                           c++;
                           temp1[j][c]=temp[j][h];
                       }
                   }
               }
           }
        int one_yes=0,one_no=0,zero_yes=0,zero_no=0;
        for(int j=1;j<=k;j++)
                {
                    if(temp1[j][0]==1 && temp1[j][1]==1) one_yes++;
                    else if(temp1[j][0]==1 && temp1[j][1]==0) one_no++;
                    else if(temp1[j][0]==0 && temp1[j][1]==1) zero_yes++;
                    else if(temp1[j][0]==0 && temp1[j][1]==0) zero_no++;
                }
        if(mode==1)
        {
            if(one_yes>=one_no)return "+ve";
            else return "-ve";
        }
        else
        {
            if(zero_yes>=zero_no)return "+ve";
            else return "-ve";
        }
    }
    
    
    public double accuracy_using_validation(ArrayList<String> new_dec){
        double acc = 0.0;
        double sum = 0.0;
        
        for(int i=1;i<validation_rows;i++)
        {
            int flg=0;int index=0;
            while(flg==0)
            {
                String[] splt=new_dec.get(index).split(",");
                int h = Integer.parseInt(splt[1]);
                if(validation[i][h]==1) 
                {
                   if((splt[2].equals("-2") && validation[i][validation_cols-1]==1)||(splt[2].equals("-3") && validation[i][validation_cols-1]==0))
                   {
                       sum++;
                       flg=1;
                   }
                   else if(!splt[2].equals("-2") && !splt[2].equals("-3"))index=Integer.parseInt(splt[2]);
                   else flg=1;
                }
                else
                {
                    if((splt[3].equals("-2") && validation[i][validation_cols-1]==1)||(splt[3].equals("-3") && validation[i][validation_cols-1]==0))
                   {
                       sum++;
                       flg=1;
                   }
                   else if(!splt[3].equals("-2") && !splt[3].equals("-3"))index=Integer.parseInt(splt[3]);
                   else flg=1;
                }
            }
        }
        
        acc= (sum/((1.0)*(validation_rows-1)))*100.0;
        return acc;
    }
   
    
    public void accuracy_using_test(){
        double acc = 0.0;
        double sum = 0.0;
        
        for(int i=1;i<test_rows;i++)
        {
            int flg=0;int index=0;
            while(flg==0)
            {
                String[] splt=decision_tree.get(index).split(",");
                int h = Integer.parseInt(splt[1]);
                if(test[i][h]==1) 
                {
                   if((splt[2].equals("-2") && test[i][test_cols-1]==1)||(splt[2].equals("-3") && test[i][test_cols-1]==0))
                   {
                       sum++;
                       flg=1;
                   }
                   else if(!splt[2].equals("-2") && !splt[2].equals("-3"))index=Integer.parseInt(splt[2]);
                   else flg=1;
                }
                else
                {
                    if((splt[3].equals("-2") && test[i][test_cols-1]==1)||(splt[3].equals("-3") && test[i][test_cols-1]==0))
                   {
                       sum++;
                       flg=1;
                   }
                   else if(!splt[3].equals("-2") && !splt[3].equals("-3"))index=Integer.parseInt(splt[3]);
                   else flg=1;
                }
            }
        }
        
        acc = (sum/((1.0)*(test_rows-1)))*100.0;
        System.out.println("\nAccuracy on test data [After Pruning]: "+acc+" %");
    }
    
}
