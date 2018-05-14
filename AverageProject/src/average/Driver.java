package average;

import java.io.IOException;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Driver extends Configured implements Tool {

	public static class Map extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private IntWritable num = new IntWritable(1);
		private Text word = new Text();		

		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			
			String[] splits = line.split(" ");
			
			if(splits != null){
				String ip = splits[0];
				String val = splits[splits.length - 1];
				
				if(isNumeric(val)){
					try {
						word.set(ip);
						num.set(Integer.parseInt(val));
						context.write(word, num);
					} catch (NumberFormatException e) {}					
				}
			}
		}		
		
		public boolean isNumeric(String str)
		{
			return str.matches("-?\\d+(\\.\\d+)?");
		}
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, Text, DoubleWritable> {

		public void reduce(Text key, Iterable<IntWritable> values,
				Reducer<Text, IntWritable, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {
			int sum = 0;
			int cnt = 0;
			for (IntWritable val : values) {
				cnt ++;
				sum += val.get();
			}
			context.write(key, new DoubleWritable(sum/cnt));
		}
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Driver(), args);
		System.exit(res);
	}

	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "Job");
		job.setJarByClass(this.getClass());
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}