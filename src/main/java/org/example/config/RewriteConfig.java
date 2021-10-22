package org.example.config;

import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;

import java.util.Map;

public class RewriteConfig {

    private Class inClass;

    private Class outClass;

    private Map<String, Object> inHints;

    private Map<String, Object> outHints;

    private String newContentType;

    private String contentType;

    private RewriteFunction rewriteFunction;

    public Class getInClass() {
        return inClass;
    }

    public RewriteConfig setInClass(Class inClass) {
        this.inClass = inClass;
        return this;
    }

    public Class getOutClass() {
        return outClass;
    }

    public RewriteConfig setOutClass(Class outClass) {
        this.outClass = outClass;
        return this;
    }

    public Map<String, Object> getInHints() {
        return inHints;
    }

    public RewriteConfig setInHints(Map<String, Object> inHints) {
        this.inHints = inHints;
        return this;
    }

    public Map<String, Object> getOutHints() {
        return outHints;
    }

    public RewriteConfig setOutHints(Map<String, Object> outHints) {
        this.outHints = outHints;
        return this;
    }

    public String getNewContentType() {
        return newContentType;
    }

    public RewriteConfig setNewContentType(String newContentType) {
        this.newContentType = newContentType;
        return this;
    }

    public RewriteFunction getRewriteFunction() {
        return rewriteFunction;
    }

    public RewriteConfig setRewriteFunction(RewriteFunction rewriteFunction) {
        this.rewriteFunction = rewriteFunction;
        return this;
    }

    public <T, R> RewriteConfig setRewriteFunction(Class<T> inClass, Class<R> outClass,
                                                   RewriteFunction<T, R> rewriteFunction) {
        setInClass(inClass);
        setOutClass(outClass);
        setRewriteFunction(rewriteFunction);
        return this;
    }

    public String getContentType() {
        return "application/json;charset=utf-8";
    }

    public RewriteConfig setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
}