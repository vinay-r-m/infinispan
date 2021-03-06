package org.infinispan.commands.remote;

import java.util.concurrent.CompletableFuture;

import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commands.VisitableCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.context.InvocationContextFactory;
import org.infinispan.interceptors.AsyncInterceptorChain;
import org.infinispan.util.ByteString;
import org.infinispan.util.concurrent.locks.RemoteLockCommand;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

/**
 * Base class for RPC commands.
 *
 * @author Mircea.Markus@jboss.com
 * @deprecated Since 9.0, it will be removed soon
 */
@Deprecated
public abstract class BaseRpcInvokingCommand extends BaseRpcCommand {

   protected AsyncInterceptorChain interceptorChain;
   protected InvocationContextFactory icf;

   private static final Log log = LogFactory.getLog(BaseRpcInvokingCommand.class);
   private static final boolean trace = log.isTraceEnabled();

   protected BaseRpcInvokingCommand(ByteString cacheName) {
      super(cacheName);
   }

   public void init(AsyncInterceptorChain interceptorChain, InvocationContextFactory icf) {
      this.interceptorChain = interceptorChain;
      this.icf = icf;
   }

   protected final Object processVisitableCommand(ReplicableCommand cacheCommand) throws Throwable {
      if (cacheCommand instanceof VisitableCommand) {
         VisitableCommand vc = (VisitableCommand) cacheCommand;
         final InvocationContext ctx = icf.createRemoteInvocationContextForCommand(vc, getOrigin());
         if (cacheCommand instanceof RemoteLockCommand) {
            ctx.setLockOwner(((RemoteLockCommand) cacheCommand).getKeyLockOwner());
         }
         if (trace) log.tracef("Invoking command %s, with originLocal flag set to %b", cacheCommand, ctx.isOriginLocal());
         return interceptorChain.invoke(ctx, vc);
      } else {
         throw new RuntimeException("Do we still need to deal with non-visitable commands? (" + cacheCommand.getClass().getName() + ")");
      }
   }

   protected final CompletableFuture<Object> processVisitableCommandAsync(ReplicableCommand cacheCommand) throws Throwable {
      if (cacheCommand instanceof VisitableCommand) {
         VisitableCommand vc = (VisitableCommand) cacheCommand;
         final InvocationContext ctx = icf.createRemoteInvocationContextForCommand(vc, getOrigin());
         if (cacheCommand instanceof RemoteLockCommand) {
            ctx.setLockOwner(((RemoteLockCommand) cacheCommand).getKeyLockOwner());
         }
         if (trace)
            log.tracef("Invoking command %s, with originLocal flag set to %b", cacheCommand, ctx
                  .isOriginLocal());
         return interceptorChain.invokeAsync(ctx, vc);
      } else {
         throw new RuntimeException(
               "Do we still need to deal with non-visitable commands? (" + cacheCommand.getClass().getName() + ")");
      }
   }
}
