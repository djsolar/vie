package com.sunmap.teleview.util;
import java.io.Serializable;

public class Tuple implements Serializable{
	private static final long serialVersionUID = 7144827877463030359L;

	public static<A,B> TwoTuple<A,B> tuple(A first,B second){
		return new TwoTuple<A,B>(first,second);
	}
	public static<A,B,C> ThreeTuple<A,B,C> tuple(A first,B second,C third){		
		return new ThreeTuple<A,B,C>(first,second,third);
	}
	public static<A,B,C,D> FourTuple<A,B,C,D> tuple(A first,B second,C third,D fourth){
		return new FourTuple<A,B,C,D>(first,second,third,fourth);
	}
	public static<A,B,C,D,E> FiveTuple<A,B,C,D,E> tuple(A first,B second,C third,D fourth,E fifth){
		return new FiveTuple<A,B,C,D,E>(first,second,third,fourth,fifth);
	}
	public static<A,B,C,D,E,F> SixTuple<A,B,C,D,E,F> tuple(A first,B second,C third,D fourth,E fifth,F sixth){
		return new SixTuple<A,B,C,D,E,F>(first,second,third,fourth,fifth,sixth);
	}
	
	
	public static class TwoTuple<A,B> implements Serializable{
		private static final long serialVersionUID = 7837596005877521650L;
		public final A first;
		public final B second;
		public TwoTuple(A first,B second){
			this.first=first;
			this.second=second;
		}
		public A getFirst()
		{
			return first;
		}
		public B getSecond()
		{
			return second;
		}
	}
	public static class ThreeTuple<A,B,C> extends TwoTuple<A,B>{
		private static final long serialVersionUID = -1212108950735315538L;
		public final C third;
		public ThreeTuple(A first,B second,C third){
			super(first,second);
			this.third=third;
		}
		public C getThird()
		{
			return third;
		}
	}
	public static class FourTuple<A,B,C,D> extends ThreeTuple<A,B,C>{
		private static final long serialVersionUID = -1411603460864143471L;
		public final D fourth;
		public FourTuple(A first,B second,C third,D fourth){
			super(first,second,third);
			this.fourth=fourth;
		}
		public D getFourth()
		{
			return fourth;
		}
	}
	public static class FiveTuple<A,B,C,D,E> extends FourTuple<A,B,C,D>{
		private static final long serialVersionUID = 5747601390394636607L;
		public final E fifth;
		public FiveTuple(A first,B second,C third,D fourth,E fifth){
			super(first,second,third,fourth);
			this.fifth=fifth;
		}
		public E getFifth()
		{
			return fifth;
		}
	}
	public static class SixTuple<A,B,C,D,E,F> extends FiveTuple<A,B,C,D,E>{
		private static final long serialVersionUID = -2623553224423408427L;
		public final F sixth;
		public SixTuple(A first,B second,C third,D fourth,E fifth,F sixth){
			super(first,second,third,fourth,fifth);
			this.sixth=sixth;
		}
		public F getSixth()
		{
			return sixth;
		}
	}
}
