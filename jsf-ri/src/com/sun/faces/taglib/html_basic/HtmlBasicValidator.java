/*
 * $Id: HtmlBasicValidator.java,v 1.10 2004/07/26 21:12:44 rlubke Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.taglib.html_basic;

import com.sun.faces.taglib.FacesValidator;
import com.sun.faces.taglib.ValidatorInfo;
import com.sun.faces.util.Util;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.faces.RIConstants;


/**
 * <p>Top level validator for the html_basic tld</p>
 *
 * @author Justyna Horwat
 * @author Ed Burns
 */
public class HtmlBasicValidator extends FacesValidator {

    //*********************************************************************
    // Validation and configuration state (protected)
    private ValidatorInfo validatorInfo;
    private CommandTagParserImpl commandTagParser;


    //*********************************************************************
    // Constructor and lifecycle management

    public HtmlBasicValidator() {
        super();
        init();
    }


    protected void init() {
        super.init();
        failed = false;
        validatorInfo = new ValidatorInfo();

        commandTagParser = new CommandTagParserImpl();
        commandTagParser.setValidatorInfo(validatorInfo);
    }


    public void release() {
        super.release();
        init();
    }


    protected DefaultHandler getSAXHandler() {
	// don't run the TLV if we're in designTime, or the RIConstants
	// says not to.
	
	if (java.beans.Beans.isDesignTime() || 
	    !RIConstants.HTML_TLV_ACTIVE) {
	    return null;
	}
	
        DefaultHandler h = new HtmlBasicValidatorHandler();
        return h;
    }


    protected String getFailureMessage(String prefix, String uri) {
        // we should only get called if this Validator failed
        Util.doAssert(failed);

        StringBuffer result = new StringBuffer();
        if (commandTagParser.hasFailed()) {
            result.append(commandTagParser.getMessage());
        }
        return result.toString();
    }
	    
    //*********************************************************************
    // SAX handler

    /**
     * The handler that provides the base of the TLV implementation.
     */
    private class HtmlBasicValidatorHandler extends DefaultHandler {

        /**
         * Parse the starting element.  Parcel out to appropriate
         * handler method.
         *
         * @param ns Element name space.
         * @param ln Element local name.
         * @param qn Element QName.
         * @param a  Element's Attribute list.
         */
        public void startElement(String ns,
                                 String ln,
                                 String qn,
                                 Attributes attrs) {
            maybeSnagTLPrefixes(qn, attrs);
            validatorInfo.setQName(qn);
            validatorInfo.setAttributes(attrs);

            commandTagParser.parseStartElement();
            if (commandTagParser.hasFailed()) {
                failed = true;
            }
        }


        /**
         * Parse the ending element. If it is a specific JSTL tag
         * make sure that the nested count is decreased.
         *
         * @param ln Element local name.
         * @param qn Element QName.
         * @param a  Element's Attribute list.
         */
        public void endElement(String ns, String ln, String qn) {
        }
    }
}
