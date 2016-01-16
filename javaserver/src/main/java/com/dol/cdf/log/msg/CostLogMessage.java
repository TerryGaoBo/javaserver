//package com.dol.cdf.log.msg;
//
//import java.io.IOException;
//
//import org.apache.log4j.Logger;
//
//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**消费记录
// * @author shaobo.yang
// *
// */
//public class CostLogMessage extends BaseLogMessage{
//	 public static Logger logger = Logger.getLogger("CostRecord");
//	private CostRecord costRecord;
//	public CostLogMessage(CostRecord costRecord) {
//		super();
//		this.costRecord=costRecord;
//	}
//	@Override
//	public String toString() {
//		try {
//			return new ObjectMapper().writeValueAsString(costRecord);
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//}
