/**
 * multipool-stats-backend is a web application which collects statistics
 * on several Switching-profit crypto-currencies mining pools and display
 * then in a Browser.
 * Copyright (C) 2014  Stratehm (stratehm@hotmail.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with multipool-stats-backend. If not, see <http://www.gnu.org/licenses/>.
 */
package strat.mining.multipool.stats.jersey.model.kraken;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pair {
	private List<String> a;
	private List<String> b;
	private List<String> c;
	private List<String> h;
	private List<String> l;
	private String o;
	private List<String> p;
	private List<String> t;
	private List<String> v;

	public List<String> getA() {
		return a;
	}

	public void setA(List<String> a) {
		this.a = a;
	}

	public List<String> getB() {
		return b;
	}

	public void setB(List<String> b) {
		this.b = b;
	}

	public List<String> getC() {
		return c;
	}

	public void setC(List<String> c) {
		this.c = c;
	}

	public List<String> getH() {
		return h;
	}

	public void setH(List<String> h) {
		this.h = h;
	}

	public List<String> getL() {
		return l;
	}

	public void setL(List<String> l) {
		this.l = l;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public List<String> getP() {
		return p;
	}

	public void setP(List<String> p) {
		this.p = p;
	}

	public List<String> getT() {
		return t;
	}

	public void setT(List<String> t) {
		this.t = t;
	}

	public List<String> getV() {
		return v;
	}

	public void setV(List<String> v) {
		this.v = v;
	}

}
