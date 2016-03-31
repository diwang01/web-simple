/**
 * 
 */
package com.letv.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Http请求的一个包装器
 * <br>
 * 此包装器负责的工作是：将底层request包装成可编辑的request
 * <br>
 * 注：此包装类不处理Multipart方式
 * @author by <a href="mailto:lizheng8318@gmail.com">lizheng</a>
 */
public class HttpServletRequestDecorator extends HttpServletRequestWrapper{
	private static Logger logger = LoggerFactory.getLogger(HttpServletRequestDecorator.class);
	/**
	 * 把Iterator包装成Enumeration的一个包装类
	 */
	private static class IteratorWrapper<E> implements Enumeration<E>{
		private Iterator<E> it;
		public IteratorWrapper(Iterator<E> it){
			this.it = it;
		}
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		public E nextElement() {
			return it.next();
		}
	}
	
	/**
	 * 包装输入流
	 */
	private static class ServletInputStreamAdpter extends ServletInputStream {
		
		private InputStream is;
		
		ServletInputStreamAdpter(InputStream is) {
			this.is = is;
		}

		@Override
		public int read() throws IOException {
			return is.read();
		}
		
	}
	
	/** 参数Map */
	protected Map<String, String[]> paramMap;

	/** json/xml request body */
	protected byte[] body;
	
	/**
	 * @param request
	 */
	public HttpServletRequestDecorator(HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * 得到对应的请求参数，这里返回的参数值已经被转码
	 * @param arg0
	 * @return 对应的请求参数值
	 */
	public String getParameter(String arg0) {
		String[] paramValues = getParameterValues(arg0);
		if (paramValues != null && paramValues.length>0) {
			return paramValues[0];
		} else{
			return null;
		}
	}

	/**
	 * @return 请求参数Map
	 */
	public Map<String, String[]> getParameterMap() {
		handleParam();	// paramMap读取
		return paramMap;
	}

	/**
	 * @return 请求参数名的枚举，这里返回的枚举包括解密请求参数之后的参数名
	 */
	public Enumeration<String> getParameterNames() {
		handleParam();	// paramMap读取
		return new IteratorWrapper<String>(paramMap.keySet().iterator());
	}
	
	/**
	 * @return 得到对应的请求参数值数组
	 */
	public String[] getParameterValues(String arg0) {
		handleParam();	// paramMap读取
		return paramMap.get(arg0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (handleInputStream()) {
			return new ServletInputStreamAdpter(new ByteArrayInputStream(body));
		} else {
			return super.getInputStream();
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequestWrapper#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		if (handleInputStream()) {
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body)));
		} else {
			return super.getReader();
		}
	}

	/**
	 * 设置可重复读取的input stream
	 * @throws java.io.IOException
	 */
	protected boolean handleInputStream() throws IOException {
		if (body == null) {
			MediaType mt = getContentType(getRequest());
			if (mt != null && MediaType.APPLICATION_JSON.isCompatibleWith(mt)) {	// XXX 这里还可以判断其它的requestbody类型
				// 如果是json数据提交，则设置可重复读取的inputstream，用来做拦截器获取
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
				byte[] buff = new byte[100];
		        int rc = 0;
		        InputStream in = getRequest().getInputStream();
		        try {
					while ((rc = in.read(buff, 0, 100)) > 0) {
					    swapStream.write(buff, 0, rc);
					}
				} finally {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
		        swapStream.flush();
				body = swapStream.toByteArray();
				swapStream.close();
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Return the {@linkplain org.springframework.http.MediaType media type} of the body, as specified by the {@code Content-Type} header.
	 * <p>Returns {@code null} when the content-type is unknown.
	 * @return the content type
	 */
	private MediaType getContentType(ServletRequest request) {
		String value = request.getContentType();
		return (value != null ? MediaType.parseMediaType(value) : null);
	}
	
	/**
	 * 处理请求中的参数
	 */
	protected void handleParam(){
		if (paramMap == null) {
			// 懒加载方式生成
			paramMap = new HashMap<String, String[]>();
			Map<?,?> map = getRequest().getParameterMap();
			for (Object key : map.keySet()) {
				String paramName = (String)key;
				String[] paramValue = (String[])map.get(key);
				paramMap.put(paramName, paramValue);
			}			
		}
	}
	
	/**
	 * 设置参数
	 * @param paramMap
	 */
	public void setParameters(Map<String, String[]> paramMap){
		logger.debug("Cus set request parameter", paramMap == null ? "nil" : paramMap.size());
		handleParam();	// paramMap读取
		this.paramMap.putAll(paramMap);
	}
	
	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + getRequest().toString();
	}
}
