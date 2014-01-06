package br.ufc.virtual.solarmobilis.util;

import java.util.ArrayList;

public class TextBlockenizer {

	protected String text;
	protected ArrayList<Integer> blockBreaks = new ArrayList<Integer>();
	protected int minBlockLength = 1400;
	protected int maxBlockLength = 1600;
	protected int currentBlock = 0;
	protected ArrayList<Integer> whiteSpacePositions = new ArrayList<Integer>();

	public TextBlockenizer(String text, int minblockLength, int maxblockLength) {
		this.minBlockLength = minblockLength;
		this.maxBlockLength = maxblockLength;
		init(text);
	}

	public TextBlockenizer(String text) {
		super();
		init(text);
	}

	protected void init(String text) {
		if (text.length() == 0)
			text = " ";
		this.text = text.replaceAll("\\s+", " ");
		if (!isOnlyOneBlock()) {
			findBlocks();
		}
	}

	public boolean isOnlyOneBlock() {
		boolean onlyOneBlock = (maxBlockLength >= text.length());
		if (onlyOneBlock) {
			blockBreaks.add(text.length());
		}
		return onlyOneBlock;
	}

	public void findBlocks() {
		int lastWhiteSpace = -1;
		int lastPontuation = -1;
		int lastBreak = 0;
		int i = 0;

		for (i = minBlockLength; i < text.length() - 1; i++) {
			if (Character.isSpaceChar(text.charAt(i))) {
				lastWhiteSpace = i;

				if (isPunctuationCharacter(text.charAt(i - 1))) {
					lastPontuation = i;
				}

			}

			if (i >= (lastBreak + maxBlockLength)) {
				if ((lastPontuation > lastBreak)) {
					lastBreak = lastPontuation;
				} else {
					if (lastWhiteSpace <= lastBreak + minBlockLength) {
						lastBreak = lastBreak + maxBlockLength;
					} else {
						lastBreak = lastWhiteSpace;
					}
				}

				blockBreaks.add(lastBreak);
				i = lastBreak + minBlockLength;
			}
		}

		if (lastBreak < i)
			blockBreaks.add(text.length());
	}

	public String getBlock(int index) {
		int start = 0;
		currentBlock = index;

		if ((index > 0) && (index < blockBreaks.size())) {
			start = blockBreaks.get(index - 1) + 1;
		}

		if (index >= blockBreaks.size()) {
			return "";
		}

		int end = blockBreaks.get(index);

		currentBlock++;
		return text.substring(start, end);
	}

	public String getFirst() {
		return getBlock(0);
	}

	public String getNext() {
		return getBlock(currentBlock);
	}

	public String getText() {
		return text;
	}

	private boolean isPunctuationCharacter(char c) {
		return c == '.' || c == '!' || c == '?' || c == ';' || c == ':'; // || c
																			// ==
																			// ','
	}

	public int size() {
		return blockBreaks.size();// - 1;
	}

	public int getCurrentBlockPosition() {
		return currentBlock;
	}

}
