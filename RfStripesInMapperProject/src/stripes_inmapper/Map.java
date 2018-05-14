package stripes_inmapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<LongWritable, Text, Text, MapWritable> {

	HashMap<String, MapWritable> h;

	@Override
	protected void setup(
			Mapper<LongWritable, Text, Text, MapWritable>.Context context)
			throws IOException, InterruptedException {
		h = new HashMap<String, MapWritable>();
	}

	@Override
	protected void map(LongWritable key, Text value,
			Mapper<LongWritable, Text, Text, MapWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString().trim();
		String[] inputs = line.split(" ");
		for (int i = 0; i < inputs.length; i++) {
			for (int j = i + 1; j < inputs.length && !inputs[i].equals(inputs[j]); j++) {
				MapWritable mapWritable = h.get(inputs[i]);
				if (mapWritable == null) {
					mapWritable = new MapWritable();
					h.put(inputs[i], mapWritable);
				}

				if (mapWritable.get(new Text(inputs[j])) == null) {
					mapWritable.put(new Text(inputs[j]), new IntWritable(1));
				} else {
					IntWritable inWritable = (IntWritable) mapWritable
							.get(new Text(inputs[j]));
					mapWritable.put(new Text(inputs[j]), new IntWritable(
							inWritable.get() + 1));
				}
			}
		}
	}

	@Override
	protected void cleanup(
			Mapper<LongWritable, Text, Text, MapWritable>.Context context)
			throws IOException, InterruptedException {
		super.cleanup(context);
		for (Entry<String, MapWritable> entry : h.entrySet()) {
			context.write(new Text(entry.getKey()), entry.getValue());
		}
	}
}
