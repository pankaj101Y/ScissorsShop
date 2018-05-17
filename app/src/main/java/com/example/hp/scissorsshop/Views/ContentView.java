package
        com.example.hp.scissorsshop.Views;

import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.scissorsshop.R;

public class ContentView extends CardView {
    private ImageView deleteIcon;
    private TextView serviceName;

    public ContentView(Context context) {
        super(context);
        init(context);
    }

    public ContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void  init(Context context){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.service_add_view, this);

        deleteIcon=findViewById(R.id.deleteService);
        serviceName=findViewById(R.id.serviceName);
    }

    public ImageView getDeleteIcon(){
        return deleteIcon;
    }

    public TextView getServiceName(){
        return serviceName;
    }
}
