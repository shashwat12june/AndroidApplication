package Network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Shashwat-PC on 15-02-2018.
 */

public class Volley_Request {
    public static Volley_Request volley_request;
    public static Context ctx;
RequestQueue requestQueue;
    private Volley_Request(Context ctx)
    {
        this.ctx=ctx;
       requestQueue= getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(ctx.getApplicationContext());

        }
        return requestQueue;
    }

    public static synchronized Volley_Request getVolleyInstance(Context context)
    {
        if(volley_request==null)
        {
            volley_request=new Volley_Request(context);
        }

        return volley_request;
    }
    public<T> void addRequestToQueue(Request<T> request)
    {
        requestQueue.add(request);
    }


}
