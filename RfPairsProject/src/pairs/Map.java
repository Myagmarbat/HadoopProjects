package pairs;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Pair, IntWritable> {
	private final static IntWritable one = new IntWritable(1);

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, Pair, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString().trim();
		String[] inputs = line.split(" ");

		for (int i = 0; i < inputs.length; i++) {
			for (int j = i + 1; j < inputs.length
					&& !inputs[i].equals(inputs[j]); j++) {
				Pair p = new Pair(inputs[i], inputs[j]);
				context.write(p, one);

				p = new Pair(inputs[i], "*");
				context.write(p, one);
			}
		}
	}
}