/**
 *
 */
package com.redhat.brq.integration.activemql.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Class representing job which will be executed by consumers.
 *
 * @author jknetl
 *
 */

@XmlRootElement
public class Job {
	@XmlTransient
	public static final int MAX_DURATION = 10;

	private String name;
	private int duration;

	public Job() {
		super();
	}

	public Job(String name, int duration) {
		super();
		this.name = name;
		setDuration(duration);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		if (duration <= 0 || duration > MAX_DURATION) {
			throw new IllegalArgumentException("Duration must be in range (0, " + MAX_DURATION + "]");
		}
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "Job [name=" + name + ", duration=" + duration + "]";
	}

}
