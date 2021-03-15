package bsplib.nmo;

import io.*;
import java.io.*;

/**
 * <h3>A nmo extraction used in nmo files.</h3>
 * <p>
 * Consists of:
 * <ul>
 *     <li>{@link #id} entity vmf id of the nmrih_extract_point entity</li>
 *     <li>{@link #name} nmrih_extract_point entity name</li>
 * </ul>
 */
public class NmoExtraction extends NmoNode {

	public NmoExtraction(DataReader reader) throws IOException {
		super(reader);
	}
}
