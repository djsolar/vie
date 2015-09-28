package com.sunmap.teleview.util;




/**
 * 1/2560坐标矩形
 * @author chenzhen
 */
public class GeoRect {
	public GeoRect() {
		this.pointLB = new GeoPoint(0, 0);
		this.pointRT = new GeoPoint(0, 0);
	}
	public GeoRect(GeoPoint pointLB, GeoPoint pointRT){
		if (pointLB == null || pointRT == null) {
			this.pointLB = new GeoPoint(0, 0);
			this.pointRT = new GeoPoint(0, 0);
		} else {
			this.pointLB = pointLB;
			this.pointRT = pointRT;
		}
	}
	public GeoRect(int left, int top, int right, int bottom){
		this.pointLB = new GeoPoint(bottom, left);
		this.pointRT = new GeoPoint(top, right);
	}
	public GeoRect(GeoRect rect) {
		if (rect == null) {
			this.pointLB = new GeoPoint(0, 0);
			this.pointRT = new GeoPoint(0, 0);
		} else {
			this.pointLB = new GeoPoint(rect.pointLB);
			this.pointRT = new GeoPoint(rect.pointRT);
		}
	}
	public GeoPoint getPointLB() {
		return pointLB;
	}
	public void setPointLB(GeoPoint pointLB) {
		this.pointLB = pointLB;
	}
	public GeoPoint getPointRT() {
		return pointRT;
	}
	public void setPointRT(GeoPoint pointRT) {
		this.pointRT = pointRT;
	}
	public int getLeft() {
		return this.pointLB.longitude;
	}
	public int getRight() {
		return this.pointRT.longitude;
	}
	public int getTop() {
		return this.pointRT.latitude;
	}
	public int getBottom() {
		return this.pointLB.latitude;
	}
	public void setLeft(int left) {
		this.pointLB.longitude = left;
	}
	public void setRight(int right) {
		this.pointRT.longitude = right;
	}
	public void setTop(int top) {
		this.pointRT.latitude = top;
	}
	public void setBottom(int bottom) {
		this.pointLB.latitude = bottom;
	}
	public boolean isIntersect(GeoRect rect){
		return Math.min(this.pointLB.longitude,this.pointRT.longitude) <= Math.max(rect.pointLB.longitude,rect.pointRT.longitude) 
				&& Math.min(rect.pointLB.longitude,rect.pointRT.longitude) <= Math.max(this.pointLB.longitude,this.pointRT.longitude)
				&& Math.min(this.pointLB.latitude,this.pointRT.latitude) <= Math.max(rect.pointLB.latitude,rect.pointRT.latitude)
				&& Math.min(rect.pointLB.latitude,rect.pointRT.latitude) <= Math.max(this.pointLB.latitude,this.pointRT.latitude);
	}
	public String toString(){
		return new String("LB:"+this.pointLB.longitude+","+this.pointLB.latitude+"RT:"+this.pointRT.longitude+","+this.pointRT.latitude);
	}
	/**
	 * 从指定矩形中拷贝数据
	 */
	public void copy(GeoRect another) {
		if (another != null) {
			this.pointLB.copy(another.pointLB);
			this.pointRT.copy(another.pointRT);
		}
	}
    /**
     * 判断该矩形是否包含指定矩形
     */
    public boolean contains(GeoRect another) {
    	return this.pointLB.longitude <= another.pointLB.longitude && this.pointRT.longitude >= another.pointRT.longitude &&
     		   this.pointLB.latitude <= another.pointLB.latitude && this.pointRT.latitude >= another.pointRT.latitude;
    }
    /**
     * 判断该矩形是否包含指定坐标
     */
    public boolean contains(int x, int y) {
    	return this.pointLB.longitude <=x && this.pointRT.longitude >= x &&
     		   this.pointLB.latitude <= y && this.pointRT.latitude >= y;
    }
    /**
     * 判断该矩形是否与指定矩形相交，并将该矩形根据相交范围进行切割
     */
    public boolean intersect(GeoRect another) {
    	return intersect(another.pointLB.longitude, another.pointRT.latitude, 
    			another.pointRT.longitude, another.pointLB.latitude);
    }
    /**
     * 判断该矩形是否与指定坐标范围相交，并将该矩形根据相交范围进行切割
     */
    public boolean intersect(int left, int top, int right, int bottom) {
        if (this.intersects(left, top, right, bottom)) {
            if (this.pointLB.longitude < left) {
                this.pointLB.longitude = left;
            }
            if (this.pointRT.latitude > top) {
                this.pointRT.latitude = top;
            }
            if (this.pointRT.longitude > right) {
                this.pointRT.longitude = right;
            }
            if (this.pointLB.latitude < bottom) {
                this.pointLB.latitude = bottom;
            }
            return true;
        }
        return false;
    }
    /**
     * 判断该矩形是否与指定坐标范围相交
     */
    public boolean intersects(int left, int top, int right, int bottom) {
        return this.pointLB.longitude < right && left < this.pointRT.longitude && 
        	   this.pointRT.latitude > bottom && top > this.pointLB.latitude;
    }
    /**
     * 判断两个矩形是否相交
     */
    public static boolean intersects(GeoRect a, GeoRect b) {
        return a.pointLB.longitude < b.pointRT.longitude && b.pointLB.longitude < a.pointRT.longitude && 
        	   a.pointRT.latitude > b.pointLB.latitude && b.pointRT.latitude > a.pointLB.latitude;
    }
    /**
     * 根据指定矩形扩充范围
     */
    public void extend(GeoRect another) {
    	if (another != null) {
			if (this.pointLB.longitude > another.pointLB.longitude) {
				this.pointLB.longitude = another.pointLB.longitude;
			}
			if (this.pointRT.longitude < another.pointRT.longitude) {
				this.pointRT.longitude = another.pointRT.longitude;
			}
			if (this.pointLB.latitude > another.pointLB.latitude) {
				this.pointLB.latitude = another.pointLB.latitude;
			}
			if (this.pointRT.latitude < another.pointRT.latitude) {
				this.pointRT.latitude = another.pointRT.latitude;
			}
		}
    }
    /**
     * 根据指定点扩充范围
     */
    public void extend(GeoPoint point) {
    	if (point != null) {
			if (this.pointLB.longitude > point.longitude) {
				this.pointLB.longitude = point.longitude;
			}
			if (this.pointRT.longitude < point.longitude) {
				this.pointRT.longitude = point.longitude;
			}
			if (this.pointLB.latitude > point.latitude) {
				this.pointLB.latitude = point.latitude;
			}
			if (this.pointRT.latitude < point.latitude) {
				this.pointRT.latitude = point.latitude;
			}
		}
    }
	
	public GeoPoint pointLB;
	public GeoPoint pointRT;
}
