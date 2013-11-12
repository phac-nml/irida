
package ca.corefacility.bioinformatics.irida.processing.annotations;

import ca.corefacility.bioinformatics.irida.processing.impl.FileProcessorAspect;

/**
 * Annotation marking a method where file post processing should be launched.  
 * This interface is picked up in a pointcut in the {@link FileProcessorAspect}
 * 
 * @see FileProcessorAspect
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public @interface EnablePostProcessing {

}
