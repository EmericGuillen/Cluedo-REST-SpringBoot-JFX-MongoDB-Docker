package cluedo.jwtTokens;

public class FabriqueTokensG {

    private static FabriqueTokensG instance = null;

    public static FabriqueTokensG getInstance(){
        if (instance == null)
            instance = new FabriqueTokensG();
        return instance;
    }


    private TokensG tokensGenerators = null;


    public TokensG getTokensGenerators(){
        if (this.tokensGenerators == null)
            this.tokensGenerators = new TokensG();
        return this.tokensGenerators;
    }
}
