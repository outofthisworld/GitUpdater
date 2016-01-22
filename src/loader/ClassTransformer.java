package loader;

/**
 * Created by Unknown on 21/01/2016.
 */
public interface ClassTransformer extends IClassTransformListener {
    void addTransFormer(IClassTransformListener transformer);

    void removeTransformer(IClassTransformListener transformer);
}
