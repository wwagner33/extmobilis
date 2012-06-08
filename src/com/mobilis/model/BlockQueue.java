package com.mobilis.model;

import java.util.LinkedList;
import java.util.Queue;

public class BlockQueue {
	private Queue<String> content = new LinkedList<String>();

	public BlockQueue() {
	}

	public Queue<String> getContent() {
		return content;
	}

	public Object peek() {
		return content.peek();
	}

	public String poll() {
		return content.poll();
	}

	public void addBlock(String block) {
		content.add(block);
	}

	public int getNumberOfBlocks() {
		return content.size();
	}
}
