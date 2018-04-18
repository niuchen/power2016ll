package com.power.base.log;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
/**
 * 通用日志拦截处理。
 * 项目名称：power2016 <br>
 * 类名称：LogAspect <br>
 * 创建时间：2016-9-5 上午9:53:17 <br>
 * @author LRF <br>
 * @version 1.0
 */
@Aspect
@Component
public class LogAspect {
//    @Autowired
//	private SessionProvider session;
//    @Resource  
//    private DaoHelper daoHelper;
//	public void setSession(SessionProvider session) {
//		this.session = session;
//	}
//    public void setDaoHelper(DaoHelper daoHelper) {
//		this.daoHelper = daoHelper;
//	}
//    @Pointcut("@annotation(com.power.common.log.Log)")
//    public void controllerAspect(){}
//    /**
//     * 
//     * @param jp
//     * @param rl
//     */
//    @AfterReturning(pointcut="controllerAspect() && @annotation(rl)")
//    public void insertLogSuccess(JoinPoint jp, Log rl){
//    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//    	String signature = jp.getSignature().toString(); //获取目标方法签名   
//        String method = signature.substring(signature.lastIndexOf("." )+1, signature.indexOf("("));   
//        AuthUserVO userVO = (AuthUserVO) session.getAttribute(request, Constants.USER_KEY);
//        
//        Map<String, Object> maps = new HashMap<String, Object>();
//	    for (Object param : request.getParameterMap().keySet()) {
//	    	if(param.equals("loginPwd")){
//	    		maps.put("loginPwd", "***");
//	    	}else{
//	    		maps.put((String)param, request.getParameter((String) param));
//	    	}
//	    }
//	    
//        SysLog log = new SysLog();
//        log.setModule(rl.m());
//        log.setMethod(method);
//        log.setOprType(rl.t());
//        log.setOprDesc(rl.desc());
//        log.setBrowser(BrowserUtils.getBrowserType(request).name());
//        log.setUid(userVO.getUid());
//        log.setUname(userVO.getRealName());
//        log.setOprTime(new Date());
//        log.setIp(IpUtils.getIpAddr(request));
//        log.setFlag("1");
//        log.setParams(JSONObject.toJSONString(maps));
//        daoHelper.insert(log);
//    }   
//   
//    //标注该方法体为异常通知，当目标方法出现异常时，执行该方法体   
//    @AfterThrowing(pointcut= "controllerAspect() && @annotation(rl)", throwing="ex")
//    public void insertLog(JoinPoint jp, Log rl,Exception ex){
//    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//      String signature = jp.getSignature().toString(); //获取目标方法签名   
//        String method = signature.substring(signature.lastIndexOf("." )+1, signature.indexOf("("));   
//        AuthUserVO userVO = (AuthUserVO) session.getAttribute(request, Constants.USER_KEY);
//        
//        Map<String, Object> maps = new HashMap<String, Object>();
//	    for (Object param : request.getParameterMap().keySet()) {
//	    	if(param.equals("loginPwd")){
//	    		maps.put("loginPwd", "***");
//	    	}else{
//	    		maps.put((String)param, request.getParameter((String) param));
//	    	}
//	    }
//        
//        SysLog log = new SysLog();
//        log.setModule(rl.m());
//        log.setMethod(method);
//        log.setOprType(rl.t());
//        log.setOprDesc(rl.desc());
//        log.setBrowser(BrowserUtils.getBrowserType(request).name());
//        log.setUid(userVO.getUid());
//        log.setUname(userVO.getRealName());
//        log.setOprTime(new Date());
//        log.setIp(IpUtils.getIpAddr(request));
//        log.setParams(JSONObject.toJSONString(maps));
//        log.setFlag("0");
//        log.setEx(ex.toString());
//        daoHelper.insert(log);
//    }  
}
