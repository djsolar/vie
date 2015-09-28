package com.mansion.tele.business.background;

import java.awt.Graphics;
/**
 * 凹变凸工具类
 * @author zhangj
 *
 */
public abstract class Anim2D extends Object {
  // default constructor
  protected Graphics animGraphics = null;

  public void register(Graphics g) { animGraphics = g; }
  public boolean animating() { return animGraphics != null; }
//  public void draw(Graphics g) { draw(g, false); }

  public abstract void draw(Graphics g);
}
