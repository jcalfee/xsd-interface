package info.jcalfee.gae.ds;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemCache {

    // private static Cache defaultCache = null;
    // private static Logger log = Logger.getLogger(MemCache.class.getName());

    private static MemcacheService service = null;

    @SuppressWarnings("unchecked")
    public static <T> T cache(T obj) {
        MemcacheService service = service();
        T cachedObj = (T) service.get(obj.hashCode());
        if (cachedObj != null)
            return cachedObj;

        service.put(obj.hashCode(), obj);
        return obj;
    }

    public static MemcacheService service() {
        if (service != null)
            return service;

        service = MemcacheServiceFactory.getMemcacheService(ConfigHelper
                .getVersion());

        return service;
    }

    // public static Cache cache() {
    // if (defaultCache != null)
    // return defaultCache;
    //
    // try {
    // defaultCache =
    // CacheManager.getInstance().getCacheFactory().createCache(
    // Collections.emptyMap());
    //
    // } catch (CacheException e) {
    // e.printStackTrace();
    // }
    // return defaultCache;
    // }
}
