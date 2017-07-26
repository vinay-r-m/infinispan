package org.infinispan.commons.dataconversion;

import org.infinispan.commons.marshall.jboss.GenericJBossMarshaller;

/**
 * @since 9.1
 */
public class GenericJbossMarshallerEncoder extends MarshallerEncoder {

   public static final GenericJbossMarshallerEncoder INSTANCE = new GenericJbossMarshallerEncoder();

   private GenericJbossMarshallerEncoder() {
      super(new GenericJBossMarshaller());
   }

}
