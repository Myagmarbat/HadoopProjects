package average_inmapper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

public class Pair implements WritableComparable<Pair> {
	public IntWritable key1;
	public IntWritable key2;

	public Pair() {
		key1 = new IntWritable(0);
		key2 = new IntWritable(0);
	}

	public Pair(int key1, int key2) {
		this.key1 = new IntWritable(key1);
		this.key2 = new IntWritable(key2);
	}

	public void setKey1(IntWritable key1) {
		this.key1 = key1;
	}

	public void setKey2(IntWritable key2) {
		this.key2 = key2;
	}

	public IntWritable getKey1() {
		return key1;
	}

	public IntWritable getKey2() {
		return key2;
	}

	@Override
	public boolean equals(Object b) {
		Pair p = (Pair) b;
		return p.key1.get() == (this.key1.get())
				&& p.key2.get() == (this.key2.get());
	}

	@Override
	public int hashCode() {
		return Objects.hash(key1.get(), key2.get());
	}

	@Override
	public String toString() {
		return "(" + key1 + ", " + key2 + ")";
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		key1.readFields(arg0);
		key2.readFields(arg0);
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		key1.write(arg0);
		key2.write(arg0);
	}

	@Override
	public int compareTo(Pair p1) {
		int k = this.key1.get() - (p1.key1.get());

		if (k != 0) {
			return k;
		} else {
			return this.key2.get() - (p1.key2.get());
		}
	}

}
