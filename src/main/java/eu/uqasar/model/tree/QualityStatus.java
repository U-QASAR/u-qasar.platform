package eu.uqasar.model.tree;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import lombok.Getter;

import java.util.Random;


@Getter
public enum QualityStatus {

	Green(0),
	Yellow(1),
	Red(2),
	Gray(3)
//	Aqua(4), 
//	Teal(5), 
//	Blue(6), 
//	Fuchsia(7), 
//	Lime(8), 
//	Maroon(9), 
//	Navy(10), 
//	Olive(11), 
//	Orange(12), 
//	Purple(13), 
//	Silver(14)
	;

	private static Random r = new Random();
	private int severity = 0;

	QualityStatus(int severity) {
		this.severity = severity;
	}

	/**
	 * Returns the status with the higher severity.
	 * 
	 * @param other
	 *            the other status to be compared with
	 * @return <code>this</code> if it has the higher priority, otherwise
	 *         <code>other</code>
	 */
	public QualityStatus compareBySeverity(QualityStatus other) {
		if (other.severity > this.severity)
			return other;
		return this;
	}
	
	public static QualityStatus randomize() {
		return values()[r.nextInt(values().length)];
	}
	
	public String getCssClassName() {
		return name().toLowerCase();
	}
	
	public int getRandomQualityValue() {
		if(this == Green) {
			return r.nextInt(34) + 67;
		} else if (this == Yellow) {
			return r.nextInt(34) + 34;
		} else {
			return r.nextInt(34);
		}
	}
	
	/**
	 * Return Basic QualityStatus for defined limits
	 * @param qualityValue
	 * @param lowerAcceptanceLimit
	 * @param upperAcceptanceLimit
	 * @return
	 */
	public static QualityStatus getQualityStatusForValue(Double qualityValue,
			double lowerAcceptanceLimit, double upperAcceptanceLimit) {
		
		if (qualityValue == null) 
			return QualityStatus.Gray;
		
		QualityStatus red, green;
		double lower, upper;
		
		red = (lowerAcceptanceLimit > upperAcceptanceLimit) ? QualityStatus.Green : QualityStatus.Red;
		green = (lowerAcceptanceLimit > upperAcceptanceLimit) ? QualityStatus.Red : QualityStatus.Green;
		lower = (lowerAcceptanceLimit > upperAcceptanceLimit) ? upperAcceptanceLimit : lowerAcceptanceLimit;
		upper = (lowerAcceptanceLimit > upperAcceptanceLimit) ? lowerAcceptanceLimit : upperAcceptanceLimit;
		
	    if (qualityValue < lower )
			return red;
	    
		if (qualityValue >= lower && qualityValue < upper) {
			return QualityStatus.Yellow;
		} 
		return green;
	}

	/**Return Basic QualityStatus for defined limits
	 * @param qualityValue
	 * @param threshold
	 * @return QualityStatus with the according color name
	 */
	public static QualityStatus getQualityStatusForValue(Double qualityValue,
			Threshold threshold) {

		if (qualityValue == null) 
			return QualityStatus.Gray;
		
		QualityStatus red, green;
		double lower, upper;
		double lowerAcceptanceLimit =  threshold.getLowerAcceptanceLimit();
		double upperAcceptanceLimit =  threshold.getUpperAcceptanceLimit();
		
		
		red = (lowerAcceptanceLimit > upperAcceptanceLimit) ? threshold.getColorUpperAcceptanceLimit() : threshold.getColorLowerAcceptanceLimit();
		green = (lowerAcceptanceLimit > upperAcceptanceLimit) ? threshold.getColorLowerAcceptanceLimit() : threshold.getColorUpperAcceptanceLimit();
		lower = (lowerAcceptanceLimit > upperAcceptanceLimit) ? upperAcceptanceLimit : lowerAcceptanceLimit;
		upper = (lowerAcceptanceLimit > upperAcceptanceLimit) ? lowerAcceptanceLimit : upperAcceptanceLimit;
		
	    if (qualityValue < lower )
			return red;
	    
		if (qualityValue >= lower && qualityValue < upper) {
			return threshold.getColorMiddAcceptanceLimit();
		} 
		return green;
	}

}
