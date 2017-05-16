class Test_jakarta_old {
    private void executeWithRetry_old(final HttpMethod method) 
            throws IOException, HttpException {
            
            /** How many times did this transparently handle a recoverable exception? */
            int recoverableExceptionCount = 0;
            int execCount = 0;
            // TODO: how do we get requestSent?
            boolean requestSent = false;
            
            // loop until the method is successfully processed, the retryHandler 
            // returns false or a non-recoverable exception is thrown
            while (true) {
                execCount++;
                requestSent = false;

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Attempt number " + execCount + " to process request");
                }
                if (!this.conn.isOpen()) {
                    // this connection must be opened before it can be used
                    // This has nothing to do with opening a secure tunnel
                    try {
                        this.conn.open();
                        if (this.conn.isProxied() && this.conn.isSecure() 
                        && !(method instanceof ConnectMethod)) {
                            // we need to create a secure tunnel before we can execute the real method
                            if (!executeConnect()) {
                                // abort, the connect method failed
                                return;
                            }
                        }
                    } catch (IOException e) {
                        releaseConnection = true;
                        throw e;
                    } catch (RuntimeException e) {
                        releaseConnection = true;
                        throw e;
                    }
                }
                try {
                    method.execute(state, this.conn);
                    break;
                } catch (HttpRecoverableException httpre) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Closing the connection.");
                    }
                    this.conn.close();
                    LOG.info("Recoverable exception caught when processing request");
                    // update the recoverable exception count.
                    recoverableExceptionCount++;
                    
                    // test if this method should be retried                
                    MethodRetryHandler handler = method.getMethodRetryHandler();
                    if (handler == null) {
                        handler = new DefaultMethodRetryHandler();
                    }
                    if (!handler.retryMethod(
                            method, 
                            this.conn, 
                            httpre, 
                            execCount, 
                            requestSent)
                    ) {
                        LOG.warn(
                            "Recoverable exception caught but MethodRetryHandler.retryMethod() "
                            + "returned false, rethrowing exception"
                        );
                        // this connection can no longer be used, it has been closed
                        releaseConnection = true;
                        throw httpre;
                    }
                }
            }
            
        }
}