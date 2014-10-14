package com.gongpingjia.gpjdetector.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.gongpingjia.gpjdetector.R;
import com.gongpingjia.gpjdetector.fragment.BrandFragment;
import com.gongpingjia.gpjdetector.fragment.ModelDetailFragment;
import com.gongpingjia.gpjdetector.fragment.ModelFragment;

public class CategoryActivity extends FragmentActivity implements
BrandFragment.OnFragmentBrandSelectionListener,
ModelFragment.OnFragmentModelSelectionListener,
ModelDetailFragment.OnFragmentModelDetailSelectionListener {

    private String brandSlug;
    private String brandName;
    private String modelSlug;
    private String modelName;
    private String modelThumbnail;
	private BrandFragment mBrandFragment;
	private ModelFragment mModelFragment;
	private ModelDetailFragment mModelDetailFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_category);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		mBrandFragment = new BrandFragment();
		mModelFragment = new ModelFragment();
		mModelDetailFragment = new ModelDetailFragment();

		transaction.add(R.id.container_brand, mBrandFragment);
		transaction.add(R.id.container_model, mModelFragment);
		transaction.add(R.id.container_modeldetail, mModelDetailFragment);
		transaction.hide(mModelFragment);
		transaction.hide(mModelDetailFragment);
		transaction.commit();

	}

	@Override
	public void onFragmentBrandSelection(String brandSlug, String brandName, String brand_logo_url) {
		

		this.brandSlug = brandSlug;
		this.brandName = brandName;

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.remove(mModelFragment);
		mModelFragment = new ModelFragment();
		mModelFragment.mBrandSlug = brandSlug;
		mModelFragment.mBrandName = brandName;
		mModelFragment.brand_logo_url = brand_logo_url;

		getSupportFragmentManager().popBackStack();


		transaction.replace(R.id.container_model, mModelFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);
		transaction.commitAllowingStateLoss();

	}

	@Override
	public void onFragmentModelSelection(String modelSlug, String modelName,
			String modelThumbnail) {
		this.modelSlug = modelSlug;
		this.modelName = modelName;
		this.modelThumbnail = modelThumbnail;

		mModelDetailFragment = new ModelDetailFragment();

		mModelDetailFragment.mBrandName = this.brandName;
		mModelDetailFragment.mBrandSlug = this.brandSlug;
		mModelDetailFragment.mModelName = this.modelName;
		mModelDetailFragment.mModelSlug = this.modelSlug;
		mModelDetailFragment.mModelThumbnail = this.modelThumbnail;

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container_modeldetail, mModelDetailFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.addToBackStack(null);

		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onFragmentModelDetailSelection(String modelDetailSlug,
			String modelDetailName,
			String modelDetailYear) {
		Intent intent = new Intent();

		intent.putExtra("brandSlug", brandSlug);
		intent.putExtra("brandName", brandName);
		intent.putExtra("modelSlug", modelSlug);
		intent.putExtra("modelName", modelName);
		intent.putExtra("modelThumbnail", modelThumbnail);
		intent.putExtra("modelDetailSlug", modelDetailSlug);
		intent.putExtra("modelDetailName", modelDetailName);
		intent.putExtra("modelDetailYear", modelDetailYear);

		setResult(RESULT_OK, intent);
		CategoryActivity.this.finish();
	}

	@Override
	public void onFragmentModelDetailNull() {
		Toast.makeText(this, "未找到对应车款信息", Toast.LENGTH_SHORT).show();
	}

}
