/**
 */
package eu.hyvar.feature.provider;

import eu.hyvar.feature.util.HyFeatureAdapterFactory;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class HyFeatureItemProviderAdapterFactory extends HyFeatureAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

	/**
	 * This constructs an instance.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public HyFeatureItemProviderAdapterFactory() {
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
		supportedTypes.add(IItemPropertySource.class);
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyFeatureModel} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyFeatureModelItemProvider hyFeatureModelItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyFeatureModel}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyFeatureModelAdapter() {
		if (hyFeatureModelItemProvider == null) {
			hyFeatureModelItemProvider = new HyFeatureModelItemProvider(this);
		}

		return hyFeatureModelItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyFeature} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyFeatureItemProvider hyFeatureItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyFeatureAdapter() {
		if (hyFeatureItemProvider == null) {
			hyFeatureItemProvider = new HyFeatureItemProvider(this);
		}

		return hyFeatureItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyGroup} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyGroupItemProvider hyGroupItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyGroup}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyGroupAdapter() {
		if (hyGroupItemProvider == null) {
			hyGroupItemProvider = new HyGroupItemProvider(this);
		}

		return hyGroupItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyVersion} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyVersionItemProvider hyVersionItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyVersion}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyVersionAdapter() {
		if (hyVersionItemProvider == null) {
			hyVersionItemProvider = new HyVersionItemProvider(this);
		}

		return hyVersionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyNumberAttribute} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyNumberAttributeItemProvider hyNumberAttributeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyNumberAttribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyNumberAttributeAdapter() {
		if (hyNumberAttributeItemProvider == null) {
			hyNumberAttributeItemProvider = new HyNumberAttributeItemProvider(this);
		}

		return hyNumberAttributeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyBooleanAttribute} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyBooleanAttributeItemProvider hyBooleanAttributeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyBooleanAttribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyBooleanAttributeAdapter() {
		if (hyBooleanAttributeItemProvider == null) {
			hyBooleanAttributeItemProvider = new HyBooleanAttributeItemProvider(this);
		}

		return hyBooleanAttributeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyStringAttribute} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyStringAttributeItemProvider hyStringAttributeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyStringAttribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyStringAttributeAdapter() {
		if (hyStringAttributeItemProvider == null) {
			hyStringAttributeItemProvider = new HyStringAttributeItemProvider(this);
		}

		return hyStringAttributeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyEnumAttribute} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyEnumAttributeItemProvider hyEnumAttributeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyEnumAttribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyEnumAttributeAdapter() {
		if (hyEnumAttributeItemProvider == null) {
			hyEnumAttributeItemProvider = new HyEnumAttributeItemProvider(this);
		}

		return hyEnumAttributeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyRootFeature} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyRootFeatureItemProvider hyRootFeatureItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyRootFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyRootFeatureAdapter() {
		if (hyRootFeatureItemProvider == null) {
			hyRootFeatureItemProvider = new HyRootFeatureItemProvider(this);
		}

		return hyRootFeatureItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyGroupComposition} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyGroupCompositionItemProvider hyGroupCompositionItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyGroupComposition}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyGroupCompositionAdapter() {
		if (hyGroupCompositionItemProvider == null) {
			hyGroupCompositionItemProvider = new HyGroupCompositionItemProvider(this);
		}

		return hyGroupCompositionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyFeatureChild} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyFeatureChildItemProvider hyFeatureChildItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyFeatureChild}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyFeatureChildAdapter() {
		if (hyFeatureChildItemProvider == null) {
			hyFeatureChildItemProvider = new HyFeatureChildItemProvider(this);
		}

		return hyFeatureChildItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyFeatureType} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyFeatureTypeItemProvider hyFeatureTypeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyFeatureType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyFeatureTypeAdapter() {
		if (hyFeatureTypeItemProvider == null) {
			hyFeatureTypeItemProvider = new HyFeatureTypeItemProvider(this);
		}

		return hyFeatureTypeItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all {@link eu.hyvar.feature.HyGroupType} instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HyGroupTypeItemProvider hyGroupTypeItemProvider;

	/**
	 * This creates an adapter for a {@link eu.hyvar.feature.HyGroupType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter createHyGroupTypeAdapter() {
		if (hyGroupTypeItemProvider == null) {
			hyGroupTypeItemProvider = new HyGroupTypeItemProvider(this);
		}

		return hyGroupTypeItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class<?>) || (((Class<?>)type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

	/**
	 * This disposes all of the item providers created by this factory. 
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void dispose() {
		if (hyFeatureModelItemProvider != null) hyFeatureModelItemProvider.dispose();
		if (hyFeatureItemProvider != null) hyFeatureItemProvider.dispose();
		if (hyGroupItemProvider != null) hyGroupItemProvider.dispose();
		if (hyVersionItemProvider != null) hyVersionItemProvider.dispose();
		if (hyNumberAttributeItemProvider != null) hyNumberAttributeItemProvider.dispose();
		if (hyBooleanAttributeItemProvider != null) hyBooleanAttributeItemProvider.dispose();
		if (hyStringAttributeItemProvider != null) hyStringAttributeItemProvider.dispose();
		if (hyEnumAttributeItemProvider != null) hyEnumAttributeItemProvider.dispose();
		if (hyRootFeatureItemProvider != null) hyRootFeatureItemProvider.dispose();
		if (hyGroupCompositionItemProvider != null) hyGroupCompositionItemProvider.dispose();
		if (hyFeatureChildItemProvider != null) hyFeatureChildItemProvider.dispose();
		if (hyFeatureTypeItemProvider != null) hyFeatureTypeItemProvider.dispose();
		if (hyGroupTypeItemProvider != null) hyGroupTypeItemProvider.dispose();
	}

}
