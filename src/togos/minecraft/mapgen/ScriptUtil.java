package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import togos.lang.RuntimeError;
import togos.lang.ScriptError;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.noise.v3.functions.ListFunctions;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.parser.TokenizerSettings;
import togos.noise.v3.parser.ast.ASTNode;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;

public class ScriptUtil
{
	static final Context STD_CONTEXT = new Context();
	static {
		STD_CONTEXT.putAll(MathFunctions.CONTEXT);
		STD_CONTEXT.putAll(ListFunctions.CONTEXT);
		STD_CONTEXT.putAll(GeneratorDefinitionFunctions.CONTEXT);
	}
	
	public static MinecraftWorldGenerator loadWorldGenerator( Reader scriptReader, TokenizerSettings tSet ) throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		
		ASTNode programAst = Parser.parse( scriptReader, tSet );
		Expression<?> program = ptb.parseExpression(programAst);
		Object v = program.bind( STD_CONTEXT ).getValue();
		if( v instanceof MinecraftWorldGenerator ) {
			return (MinecraftWorldGenerator)v;
		} else {
			throw new RuntimeError( "Program did not return a world generator, but "+v.getClass(), program.sLoc);
		}
	}
	
	public static MinecraftWorldGenerator loadWorldGenerator( File scriptFile, int tabWidth ) throws Exception {
		FileReader r = new FileReader(scriptFile);
		try {
			return loadWorldGenerator( r, new TokenizerSettings(scriptFile.getPath(), 1, 1, tabWidth) );
		} finally {
			r.close();
		}
	}
	
	public static Binding<?> bind( String tnl, Context ctx, TokenizerSettings tSet ) throws ScriptError {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		try {
	        return ptb.parseExpression(Parser.parse(tnl, tSet)).bind(ctx);
		} catch( ScriptError e ) {
			throw e;
		} catch( RuntimeException e ) {
			throw e;
        } catch( Exception e ) {
	        throw new RuntimeException(e);
        }
	}
	
	public static Object eval( String tnl, Context ctx, TokenizerSettings tSet ) throws Exception {
		return bind(tnl, ctx, tSet).getValue();
	}
}
