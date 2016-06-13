/**
 * Copyright (C) 2016 X-Forever.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macrossx.chatbotx.algorithm.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.macrossx.chatbotx.algorithm.segment.WordSegmentation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelationVectorModel implements AlgorithmModel{
	private List<String> list1;
	private List<String> list2;
	private double delta = 1.3;
	public RelationVectorModel(String sentence1,String sentence2, WordSegmentation segmentation){
		list1 = segmentation.seggmentation(sentence1);
		list2 = segmentation.seggmentation(sentence2);
	}
	
	public RelationVectorModel(String sentence1,String sentence2, WordSegmentation segmentation, double delta){
		list1 = segmentation.seggmentation(sentence1);
		list2 = segmentation.seggmentation(sentence2);
		this.delta= delta;	
	}
	
	public double caculate() {
		if(list1.isEmpty()||list2.isEmpty())return 0.0;
		
		List<String> t1, t2 = null;
		if (list1.size() < list2.size()) {
			t1 = list1;
			t2 = list2;
		} else {
			t1 = list2;
			t2 = list1;
		}
		double[] tb1 = new double[t1.size()];
		for (int i = 0; i < tb1.length; i++) {
			tb1[i] = 1 /(double) tb1.length;
			if (i == 0 || i==t1.size()-1)
				continue;
			if (hasBackKeyword(t1.get(i + 1), t1.get(i), t2)) {
				tb1[i] = tb1[i] * delta;
			}
			if (hasFrontKeyword(t1.get(i - 1), t1.get(i), t2)) {
				tb1[i] = tb1[i] * delta;
			}
		}
		
		List<Double> tel = Lists.newArrayList();
		for(int i=0;i<t1.size();i++){
			if(t2.contains(t1.get(i))){
				tel.add(tb1[i]);
			}
		}
		double d1 = 0;
		for(int i=0;i<tel.size();i++){
			d1= d1+tel.get(i).doubleValue();
		}
		double d2 =0;
		for(int i=0;i<tb1.length;i++){
			d2=d2+tb1[i];
		}
		return d1/d2*((double)t1.size()/(double)t2.size());
	}
	
	private boolean hasFrontKeyword(String frontKeyword, String keyword, List<String> target) {
		for (int i = 0; i < target.size(); i++) {
			if (keyword.equals(target.get(i))) {
				if (i != 0 && frontKeyword.equals(target.get(i - 1)))
					return true;
			}
		}
		return false;
	}

	private boolean hasBackKeyword(String frontKeyword, String keyword, List<String> target) {
		for (int i = 0; i < target.size(); i++) {
			if (keyword.equals(target.get(i))) {
				if (i != (target.size() - 1) && frontKeyword.equals(target.get(i + 1)))
					return true;
			}
		}
		return false;
	}

/*
	public static void main(String... s) {

		String s1 = "我想去上海图书馆";
		String s2 = "上海图书馆怎么走";
		System.out.println(s1 + ":" + s2 + "-》相似度");
		RelationVectorModel tf = new RelationVectorModel(s1, s2,  HanNLPKeywordSegment.newHanNPLKeywordSegment(),	 1.3);
		System.out.println(tf.caculate());
//		System.out.println(new Double(tf.caculate1()));

		// HashSet<String> set = Sets.newHashSet();
		// for(Term t :HanLP.segment(s1)){
		// set.add(t.word);
		// }
		// for(Term t:HanLP.segment(s2)){
		// set.add(t.word);
		// }
		// System.out.println(set);
	}
	*/
}
