package com.dol.cdf.common.config;

import java.io.IOException;
import java.util.List;

import com.dol.cdf.common.bean.VariousItemEntry;
import com.dol.cdf.common.bean.VariousItemRateEntry;
import com.dol.cdf.common.collect.Range;
import com.dol.cdf.common.constant.ConstTypeEnum;
import com.dol.cdf.common.gamefunction.ConditionGF;
import com.dol.cdf.common.gamefunction.EffectGF;
import com.dol.cdf.common.gamefunction.GameFunctionFactory;
import com.dol.cdf.common.gamefunction.IConditionGF;
import com.dol.cdf.common.gamefunction.IEffectGF;
import com.dol.cdf.common.gamefunction.IGameFunction;
import com.dol.cdf.common.gamefunction.gfi.GFIType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 对象反序列化工厂，所有自定义的反序列化的对象放在这里
 * 
 * @author zhoulei
 * 
 */
public class BeanDeserializerFactory {

	public static Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	static class IntArrayDeserializer extends JsonDeserializer<int[]> {
		@Override
		public int[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (int[]) ConstTypeEnum.INT_LIST.getValue(text);
		}
	}

	static class StringArrayDeserializer extends JsonDeserializer<String[]> {
		@Override
		public String[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (String[]) ConstTypeEnum.STRING_LIST.getValue(text);
		}
	}

	static class RangeDeserializer extends JsonDeserializer<Range<Integer>> {
		@SuppressWarnings("unchecked")
		@Override
		public Range<Integer> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (Range<Integer>) ConstTypeEnum.RANGE_INT.getValue(text);
		}
	}


	static class ObjectArrayDeserializer extends JsonDeserializer<Object[]> {
		@Override
		public Object[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return EMPTY_OBJECT_ARRAY;
		}
	}

	static class ConditionGFArrayDeserializer extends JsonDeserializer<IConditionGF[]> {
		@Override
		public IConditionGF[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			List<IGameFunction> create = GameFunctionFactory.getInstance().create(text, GFIType.GFI_TYPE_CONDITION);
			IConditionGF[] confidtionGF = new ConditionGF[create.size()];
			for (int i = 0; i < create.size(); i++) {
				confidtionGF[i] = (ConditionGF) create.get(i);
			}
			return confidtionGF;
		}
	}

	static class EffectGFArrayDeserializer extends JsonDeserializer<IEffectGF[]> {
		@Override
		public IEffectGF[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			List<IGameFunction> create = GameFunctionFactory.getInstance().create(text, GFIType.GFI_TYPE_EFFECT);
			IEffectGF[] effectGf = new EffectGF[create.size()];
			for (int i = 0; i < create.size(); i++) {
				effectGf[i] = (EffectGF) create.get(i);
			}
			return effectGf;
		}
	}
	
	static class VariousItemEntryDeserializer extends JsonDeserializer<VariousItemEntry[]> {
		@Override
		public VariousItemEntry[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (VariousItemEntry[]) ConstTypeEnum.VIENTRY_LIST.getValue(text);
		}
	}
	
	static class VariousItemRateEntryDeserializer extends JsonDeserializer<VariousItemRateEntry[]> {
		@Override
		public VariousItemRateEntry[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (VariousItemRateEntry[]) ConstTypeEnum.VIENTRY_RATE_LIST.getValue(text);
		}
	}
	
	static class ListArrayDeserializer extends JsonDeserializer<List[]> {
		@Override
		public List[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			String text = jp.getText();
			return (List[]) ConstTypeEnum.INT_INT_LIST.getValue(text);
		}
	}
}
