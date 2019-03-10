
package foo;

import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.GenericOptionsParser;

import hw1_dg.MiscUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

        
public class tophash {
        
 public static class mapHashes extends Mapper<LongWritable, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();
        
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
        
        	String token = tokenizer.nextToken();
            token = token.toLowerCase();
            if (token.startsWith("#")) {
                word.set(token);
                context.write(word, one);
            }
        	
        	/*word.set(tokenizer.nextToken());
            context.write(word, one);*/
        }
    }
 } 
        
 public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {

	private Map<Text, IntWritable> countMap = new HashMap<>();
	 
    public void reduce(Text key, Iterable<IntWritable> values, Context context) 
      throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable val : values) {
            sum += val.get();
        }
        
        //don't write out, instead add to map
        //context.write(key, new IntWritable(sum));
        
        countMap.put(new Text(key), new IntWritable(sum));
    }
 }
 
 
 @Override
 protected void cleanup(Context context) throws IOException, InterruptedException {

     Map<Text, IntWritable> sortedMap = MiscUtils.sortByValues(countMap);

     int counter = 0;
     for (Text key : sortedMap.keySet()) {
         if (counter++ == 20) {
             break;
         }
         context.write(key, sortedMap.get(key));
     }
}
 
 
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
        Job job = new Job(conf, "tophash");
    
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
        
    job.setMapperClass(mapHashes.class);
    job.setReducerClass(Reduce.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    
    job.setJarByClass(tophash.class);
    
    job.waitForCompletion(true);
 }
        
}