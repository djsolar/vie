/**
 * 
 */
package com.sunmap.teleview.element.mm.data;

import java.util.Comparator;

/**
 * @author lijingru
 *
 */
public class SpeRoad {
	Road road = null;
	MMBlockID blockID = null;
	public int x = 0, y = 0;

	public SpeRoad(Road road, MMBlockID blockID) {
		this.road = road;
		this.blockID = blockID;
	}

	public Road getRoad() {
		return road;
	}

	public MMBlockID getBlockID() {
		return blockID;
	}

	public static Comparator<SpeRoad> comparebyroadid = new Comparator<SpeRoad>() {
		public int compare(SpeRoad left, SpeRoad right) {
			if (left.road.roadID > right.road.roadID) {
				return 1;
			}
			if (left.road.roadID < right.road.roadID) {
				return -1;
			}

			return 0;
		}
	};

	public static Comparator<SpeRoad> comparebyBlockId = new Comparator<SpeRoad>() {
		public int compare(SpeRoad left, SpeRoad right) {
			return MMBlockID.compareByIndex.compare(left.blockID, right.blockID);
		}
	};

	@Override
	public boolean equals(Object otr) {
		SpeRoad oth = (SpeRoad) otr;
		return this.road.equals(oth.road);
	}

}
