package com.polydes.sceneplugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Game;
import stencyl.core.lib.ResourceTypes;
import stencyl.core.lib.actor.ActorInstance;
import stencyl.sw.SW;
import stencyl.sw.app.doc.AbstractEditor;
import stencyl.sw.app.doc.StencylDoc;
import stencyl.sw.editors.scene.Designer;
import stencyl.sw.editors.scene.SceneEditor;
import stencyl.sw.editors.scene.SceneTab;
import stencyl.sw.editors.scene.actor.DefaultActorRenderer;
import stencyl.sw.ext.BaseExtension;
import stencyl.sw.ext.OptionsPanel;
import stencyl.sw.ext.ResourceEditorExtension;

public class ScenePluginExtension extends BaseExtension implements ResourceEditorExtension
{
	@Override
	public void onStartup()
	{
		super.onStartup();
		
		isInMenu = false;
		isInGameCenter = false;
		
		SW.get().getResourceEditorExtensionManager().registerExtension(ResourceTypes.scene, this);
	}
	
	@Override
	public void onDestroy()
	{
		SW.get().getResourceEditorExtensionManager().unregisterExtension(ResourceTypes.scene, this);
	}
	
	@Override
	public void resourceOpened(AbstractResource resource, StencylDoc doc, AbstractEditor editor)
	{
		if(editor instanceof SceneEditor)
		{
			SceneEditor scEditor = (SceneEditor) editor;
			SceneTab scTab = (SceneTab) scEditor.getEditorComponent();
			Designer designer = scTab.getCanvas();
			designer.getRenderer().setActorRenderer(new StrangeActorRenderer(designer));
		}
	}
	
	public class StrangeActorRenderer extends DefaultActorRenderer
	{
		private BufferedImage fakeActorImage;
		
		public StrangeActorRenderer(Designer parent)
		{
			super(parent);
			
			fakeActorImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2 = (Graphics2D) fakeActorImage.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			
			List<Color> colors = Arrays.asList(Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA);
			for(int i = 0; i < colors.size(); ++i)
			{
				g2.setColor(colors.get(i));
				g2.fillOval(5*i, 5*i, 60-10*i, 60-10*i);
			}
			
			g2.dispose();
		}
		
		@Override
		public void drawActor(Graphics2D g2, ActorInstance actor)
		{
			prepareGraphics(g2, actor);
			
			//Scaling values
			int sw = (int) (actor.getWidth() * actor.getScaleX());
			int sh = (int) (actor.getHeight() * actor.getScaleY());
			int sx = actor.getX() - ((sw - actor.getWidth()) / 2);
			int sy = actor.getY() - ((sh - actor.getHeight()) / 2);
			
			g2.drawImage(getImageForActor(parent, actor), sx + offX, sy + offY, sw, sh, null);
			
			restoreGraphics(g2, actor);
		}
		
		@Override
		public Image getImageForActor(Designer parent, ActorInstance actor)
		{
			return fakeActorImage;
		}
	}
	
	@Override public void onActivate() {}
	@Override public void onGameSave(Game game) {}
	@Override public void onGameOpened(Game game) {}
	@Override public void onGameClosed(Game game) {}
	@Override protected boolean hasOptions() { return false; }
	@Override public OptionsPanel onOptions() { return null; }
	@Override public void onInstall() {}
	@Override public void onUninstall() {}
}