package bsplib.nmo;

import io.*;
import java.io.*;

public class NmoNode {

	public int id;
	public String name;

	public NmoNode(DataReader reader) throws IOException {
		id = reader.readInt();
		name = reader.readStringNull();
	}
}
