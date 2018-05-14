package average_inmapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

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
			Mapper<LongWritable, Text, Text, Pair> {
		private Text word = new Text();		
		private HashMap<String, Pair> h;
		
		@Override
		protected void setup(
				Mapper<LongWritable, Text, Text, Pair>.Context context)
				throws IOException, InterruptedException {
			super.setup(context);
				h = new HashMap<String, Pair>();
		}

		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			
			String[] splits = line.split(" ");
			
			if(splits != null){
				String ip = splits[0];
				String numStr = splits[splits.length - 1];
				
				if(isNumeric(numStr)){			
					try {
						int num = Integer.parseInt(numStr);
						if(!h.containsKey(ip)){
							h.put(ip, new Pair(num, 1));
						} else {
							Pair p = h.get(ip);
							p.setKey1(new IntWritable(p.getKey1().get() + num));
							p.setKey2(new IntWritable(p.getKey2().get() + 1));
							h.put(ip, p);
						}
					} catch (NumberFormatException e) {}
				}
			}
		}		
		
		@Override
		protected void cleanup(
				Mapper<LongWritable, Text, Text, Pair>.Context context)
				throws IOException, InterruptedException {
			for(Entry<String, Pair> entry: h.entrySet()){
				word.set( entry.getKey() );
				context.write(word, entry.getValue());
			}
		}
		
		public boolean isNumeric(String str)
		{
			return str.matches("-?\\d+(\\.\\d+)?");
		}
	}

	public static class Reduce extends
			Reducer<Text, Pair, Text, DoubleWritable> {
		@Override
		public void reduce(Text key, Iterable<Pair> values,
				Reducer<Text, Pair, Text, DoubleWritable>.Context context) throws IOException, InterruptedException {
			int sum = 0;
			int cnt = 0;
			for (Pair val : values) {				
				sum += val.getKey1().get();
				cnt += val.getKey2().get();
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
		job.setMapOutputValueClass(Pair.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

}